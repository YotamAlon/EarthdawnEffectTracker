package com.example.earthdawneffecttracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.earthdawneffecttracker.data.EarthdawnPresets
import com.example.earthdawneffecttracker.model.AggregatedEffect
import com.example.earthdawneffecttracker.model.Condition
import com.example.earthdawneffecttracker.model.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EffectViewModel : ViewModel() {

    private val _effects = MutableStateFlow<List<Effect>>(emptyList())
    val effects: StateFlow<List<Effect>> = _effects.asStateFlow()

    private val _conditions = MutableStateFlow<List<Condition>>(emptyList())
    val conditions: StateFlow<List<Condition>> = _conditions.asStateFlow()

    private val _aggregatedEffects = MutableStateFlow<List<AggregatedEffect>>(emptyList())
    val aggregatedEffects: StateFlow<List<AggregatedEffect>> = _aggregatedEffects.asStateFlow()

    private val _presetTargets = MutableStateFlow(EarthdawnPresets.PRESET_ATTRIBUTES)
    val presetTargets: StateFlow<List<String>> = _presetTargets.asStateFlow()

    private val _presetConditions = MutableStateFlow(EarthdawnPresets.PRESET_CONDITIONS)
    val presetConditions: StateFlow<List<String>> = _presetConditions.asStateFlow()

    /**
     * Adds a new effect to tracking.
     * @return true if added successfully, false if validation failed.
     */
    fun addEffect(target: String, amount: Int, rounds: Int): Boolean {
        val trimmedTarget = target.trim()
        if (trimmedTarget.isEmpty() || (rounds <= 0) || (amount == 0)) {
            return false
        }

        val newEffect = Effect(
            target = trimmedTarget,
            amount = amount,
            roundsRemaining = rounds,
        )

        val updatedEffects = _effects.value + newEffect
        _effects.value = updatedEffects
        recalculateAggregated(updatedEffects, _conditions.value)

        // Ensure custom target is added to options list if not already present
        if (_presetTargets.value.none { it.equals(trimmedTarget, ignoreCase = true) }) {
            _presetTargets.value += trimmedTarget
        }

        return true
    }

    /**
     * Advances to the next round, decrementing rounds remaining on all active timed effects.
     * Effects reaching 0 rounds are removed.
     * @return Number of effects that expired in this round.
     */
    fun finishRound(): Int {
        val currentList = _effects.value
        val currentConds = _conditions.value
        if (currentList.isEmpty()) {
            return 0
        }

        val updatedList = mutableListOf<Effect>()
        var expiredCount = 0

        for (effect in currentList) {
            val remaining = effect.roundsRemaining - 1
            if (remaining > 0) {
                updatedList.add(effect.copy(roundsRemaining = remaining))
            } else {
                expiredCount++
            }
        }

        _effects.value = updatedList
        recalculateAggregated(updatedList, currentConds)

        return expiredCount
    }

    /**
     * Removes an individual effect manually.
     */
    fun removeEffect(effectId: String) {
        val updatedEffects = _effects.value.filterNot { it.id == effectId }
        _effects.value = updatedEffects
        recalculateAggregated(updatedEffects, _conditions.value)
    }

    /**
     * Adds a new status condition. If already present, increments its stacks.
     * @return true if added/updated successfully, false if validation failed.
     */
    fun addCondition(name: String): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return false
        }

        val currentConds = _conditions.value
        val existingIndex = currentConds.indexOfFirst { it.name.equals(trimmedName, ignoreCase = true) }

        val updatedConds = if (existingIndex >= 0) {
            currentConds.mapIndexed { index, cond ->
                if (index == existingIndex) cond.copy(stacks = cond.stacks + 1) else cond
            }
        } else {
            currentConds + Condition(name = trimmedName, stacks = 1)
        }

        _conditions.value = updatedConds
        recalculateAggregated(_effects.value, updatedConds)

        if (_presetConditions.value.none { it.equals(trimmedName, ignoreCase = true) }) {
            _presetConditions.value += trimmedName
        }

        return true
    }

    /**
     * Updates the stack count of a specific condition.
     */
    fun updateConditionStacks(conditionId: String, delta: Int) {
        val updatedConds = _conditions.value.map { cond ->
            if (cond.id == conditionId) {
                val newStacks = (cond.stacks + delta).coerceAtLeast(1)
                cond.copy(stacks = newStacks)
            } else {
                cond
            }
        }
        _conditions.value = updatedConds
        recalculateAggregated(_effects.value, updatedConds)
    }

    /**
     * Removes an individual condition manually.
     */
    fun removeCondition(conditionId: String) {
        val updatedConds = _conditions.value.filterNot { it.id == conditionId }
        _conditions.value = updatedConds
        recalculateAggregated(_effects.value, updatedConds)
    }

    /**
     * Resets all effects and conditions.
     */
    fun resetAll() {
        _effects.value = emptyList()
        _conditions.value = emptyList()
        recalculateAggregated(emptyList(), emptyList())
    }

    private fun recalculateAggregated(effects: List<Effect>, conditions: List<Condition>) {
        val translatedEffects = getTranslatedEffects(conditions)
        val allEffects = effects + translatedEffects

        val grouped = allEffects.groupBy { it.target }

        val allActionTestsAmount = grouped["All action tests"]?.sumOf { it.amount } ?: 0

        val aggregatedList = grouped.map { (target, list) ->
            val rawSum = list.sumOf { it.amount }
            val finalTotal = if (target == "All action tests" || isDefenseOrArmorOrMovement(target)) {
                rawSum
            } else {
                rawSum + allActionTestsAmount
            }
            AggregatedEffect(
                target = target,
                totalAmount = finalTotal,
                activeCount = list.size,
            )
        }.sortedBy {
            if (it.target == "All action tests") "" else it.target
        }

        _aggregatedEffects.value = aggregatedList
    }

    private fun isDefenseOrArmorOrMovement(target: String): Boolean {
        val lower = target.lowercase()
        return lower.contains("defense") || lower.contains("armor") || lower.contains("movement")
    }

    private fun getTranslatedEffects(conditions: List<Condition>): List<Effect> {
        val translated = mutableListOf<Effect>()
        for (c in conditions) {
            val s = c.stacks
            val nameLower = c.name.lowercase()
            when {
                nameLower.contains("blindsided") -> {
                    translated.add(Effect(target = "Physical Defense", amount = -2 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Mystic Defense", amount = -2 * s, roundsRemaining = 999))
                }
                nameLower.contains("harried") || nameLower.contains("entangled") -> {
                    val penalty = -(1 + s)
                    translated.add(Effect(target = "All action tests", amount = penalty, roundsRemaining = 999))
                    translated.add(Effect(target = "Physical Defense", amount = penalty, roundsRemaining = 999))
                    translated.add(Effect(target = "Mystic Defense", amount = penalty, roundsRemaining = 999))
                }
                nameLower.contains("overwhelmed") -> {
                    translated.add(Effect(target = "All action tests", amount = -1 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Physical Defense", amount = -1 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Mystic Defense", amount = -1 * s, roundsRemaining = 999))
                }
                nameLower.contains("knocked down") -> {
                    translated.add(Effect(target = "All action tests", amount = -3 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Physical Defense", amount = -3 * s, roundsRemaining = 999))
                }
                nameLower.contains("surprised") -> {
                    translated.add(Effect(target = "All action tests", amount = -3 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Physical Defense", amount = -3 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Mystic Defense", amount = -3 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "Social Defense", amount = -3 * s, roundsRemaining = 999))
                }
                nameLower.contains("partial darkness") -> {
                    translated.add(Effect(target = "All action tests", amount = -2 * s, roundsRemaining = 999))
                }
                nameLower.contains("full darkness") || nameLower.contains("blindness") -> {
                    translated.add(Effect(target = "All action tests", amount = -4 * s, roundsRemaining = 999))
                }
                nameLower.contains("impaired movement") -> {
                    translated.add(Effect(target = "Movement Rate", amount = -5 * s, roundsRemaining = 999))
                    translated.add(Effect(target = "All action tests", amount = -2 * s, roundsRemaining = 999))
                }
                nameLower.contains("wound") -> {
                    translated.add(Effect(target = "All action tests", amount = -1 * s, roundsRemaining = 999))
                }
                nameLower.contains("grappled") -> {
                    translated.add(Effect(target = "Movement Rate", amount = -99, roundsRemaining = 999))
                }
            }
        }
        return translated
    }
}
