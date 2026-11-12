package com.example.earthdawneffecttracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.earthdawneffecttracker.data.EarthdawnPresets
import com.example.earthdawneffecttracker.model.AggregatedEffect
import com.example.earthdawneffecttracker.model.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EffectViewModel : ViewModel() {

    private val _effects = MutableStateFlow<List<Effect>>(emptyList())
    val effects: StateFlow<List<Effect>> = _effects.asStateFlow()

    private val _aggregatedEffects = MutableStateFlow<List<AggregatedEffect>>(emptyList())
    val aggregatedEffects: StateFlow<List<AggregatedEffect>> = _aggregatedEffects.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _presetTargets = MutableStateFlow(EarthdawnPresets.PRESET_ATTRIBUTES)
    val presetTargets: StateFlow<List<String>> = _presetTargets.asStateFlow()

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
        recalculateAggregated(updatedEffects)

        // Ensure custom target is added to options list if not already present
        if (_presetTargets.value.none { it.equals(trimmedTarget, ignoreCase = true) }) {
            _presetTargets.value += trimmedTarget
        }

        return true
    }

    /**
     * Advances to the next round, decrementing rounds remaining on all effects.
     * Effects reaching 0 rounds are removed.
     * @return Number of effects that expired in this round.
     */
    fun finishRound(): Int {
        val currentList = _effects.value
        if (currentList.isEmpty()) {
            _currentRound.value += 1
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
        _currentRound.value += 1
        recalculateAggregated(updatedList)

        return expiredCount
    }

    /**
     * Removes an individual effect manually.
     */
    fun removeEffect(effectId: String) {
        val updatedEffects = _effects.value.filterNot { it.id == effectId }
        _effects.value = updatedEffects
        recalculateAggregated(updatedEffects)
    }

    /**
     * Resets all effects and resets round count back to 1.
     */
    fun resetAll() {
        _effects.value = emptyList()
        _currentRound.value = 1
        recalculateAggregated(emptyList())
    }

    private fun recalculateAggregated(effects: List<Effect>) {
        val grouped = effects.groupBy { it.target }
        val aggregatedList = grouped.map { (target, list) ->
            AggregatedEffect(
                target = target,
                totalAmount = list.sumOf { it.amount },
                activeCount = list.size,
            )
        }.sortedBy { it.target }

        _aggregatedEffects.value = aggregatedList
    }
}
