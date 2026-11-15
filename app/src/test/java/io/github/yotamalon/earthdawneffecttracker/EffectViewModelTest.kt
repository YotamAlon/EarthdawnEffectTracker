package io.github.yotamalon.earthdawneffecttracker

import io.github.yotamalon.earthdawneffecttracker.viewmodel.EffectViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EffectViewModelTest {

    private lateinit var viewModel: EffectViewModel

    @Before
    fun setUp() {
        viewModel = EffectViewModel()
    }

    @Test
    fun `test addEffect adds new effect and aggregates total correctly`() {
        val success1 = viewModel.addEffect("Physical Defense", 2, 3)
        val success2 = viewModel.addEffect("Physical Defense", 3, 2)
        val success3 = viewModel.addEffect("Initiative", -1, 4)

        assertTrue(success1)
        assertTrue(success2)
        assertTrue(success3)

        assertEquals(3, viewModel.effects.value.size)

        val aggregated = viewModel.aggregatedEffects.value
        assertEquals(2, aggregated.size)

        val physDef = aggregated.find { it.target == "Physical Defense" }
        val init = aggregated.find { it.target == "Initiative" }

        assertEquals(5, physDef?.totalAmount)
        assertEquals(2, physDef?.activeCount)
        assertEquals(-1, init?.totalAmount)
        assertEquals(1, init?.activeCount)
    }

    @Test
    fun `test addEffect rejects invalid input`() {
        assertFalse(viewModel.addEffect("", 2, 3))
        assertFalse(viewModel.addEffect("  ", 2, 3))
        assertFalse(viewModel.addEffect("Armor", 0, 3))
        assertFalse(viewModel.addEffect("Armor", 2, 0))
        assertFalse(viewModel.addEffect("Armor", 2, -1))
    }

    @Test
    fun `test finishRound decrements rounds and removes expired effects`() {
        viewModel.addEffect("Physical Defense", 2, 1) // Expire in 1 round
        viewModel.addEffect("Initiative", -2, 2)       // Expire in 2 rounds

        // Finish round 1
        val expiredRound1 = viewModel.finishRound()
        assertEquals(1, expiredRound1) // 1 expired (Physical Defense)
        assertEquals(1, viewModel.effects.value.size)
        assertEquals("Initiative", viewModel.effects.value[0].target)
        assertEquals(1, viewModel.effects.value[0].roundsRemaining)

        // Finish round 2
        val expiredRound2 = viewModel.finishRound()
        assertEquals(1, expiredRound2) // 1 expired (Initiative)
        assertEquals(0, viewModel.effects.value.size)
        assertTrue(viewModel.aggregatedEffects.value.isEmpty())
    }

    @Test
    fun `test removeEffect removes specific effect`() {
        viewModel.addEffect("Physical Defense", 2, 3)
        val effectId = viewModel.effects.value[0].id

        viewModel.removeEffect(effectId)
        assertTrue(viewModel.effects.value.isEmpty())
        assertTrue(viewModel.aggregatedEffects.value.isEmpty())
    }

    @Test
    fun `test resetAll clears everything`() {
        viewModel.addEffect("Physical Defense", 2, 3)
        viewModel.addEffect("Initiative", -1, 4)
        viewModel.addCondition("Harried")
        viewModel.finishRound()

        assertEquals(2, viewModel.effects.value.size)
        assertEquals(1, viewModel.conditions.value.size)

        viewModel.resetAll()

        assertTrue(viewModel.effects.value.isEmpty())
        assertTrue(viewModel.conditions.value.isEmpty())
        assertTrue(viewModel.aggregatedEffects.value.isEmpty())
    }

    @Test
    fun `test Harried stack 1 is -2 and stack 2 is -3`() {
        viewModel.addCondition("Harried")
        assertEquals(1, viewModel.conditions.value.size)
        assertEquals(1, viewModel.conditions.value[0].stacks)

        // Stack 1: -2 All action tests, -2 Physical Defense, -2 Mystic Defense
        var aggregated = viewModel.aggregatedEffects.value
        assertEquals(-2, aggregated.find { it.target == "All action tests" }?.totalAmount)
        assertEquals(-2, aggregated.find { it.target == "Physical Defense" }?.totalAmount)

        // Increment stack to 2 -> penalty becomes -(1 + 2) = -3
        viewModel.addCondition("Harried")
        assertEquals(2, viewModel.conditions.value[0].stacks)

        aggregated = viewModel.aggregatedEffects.value
        assertEquals(-3, aggregated.find { it.target == "All action tests" }?.totalAmount)
        assertEquals(-3, aggregated.find { it.target == "Physical Defense" }?.totalAmount)
    }

    @Test
    fun `test All action tests is displayed cleanly and factors into specific test totals`() {
        viewModel.addCondition("Wound") // 1 wound -> -1 All action tests
        viewModel.addEffect("Spellcasting Step", 3, 2) // +3 to Spellcasting Step

        val aggregated = viewModel.aggregatedEffects.value

        val allTests = aggregated.find { it.target == "All action tests" }
        val spellcasting = aggregated.find { it.target == "Spellcasting Step" }

        assertEquals(-1, allTests?.totalAmount)
        // Spellcasting total = +3 (specific) + -1 (All action tests) = +2
        assertEquals(2, spellcasting?.totalAmount)
    }

    @Test
    fun `test removeCondition removes specific condition`() {
        viewModel.addCondition("Harried")
        val conditionId = viewModel.conditions.value[0].id

        viewModel.removeCondition(conditionId)
        assertTrue(viewModel.conditions.value.isEmpty())
    }

    @Test
    fun `test finishRound does not affect conditions`() {
        viewModel.addCondition("Harried")
        viewModel.addEffect("Initiative", -1, 1)

        assertEquals(1, viewModel.conditions.value.size)

        viewModel.finishRound()

        // Effect expired and removed, but condition remains
        assertTrue(viewModel.effects.value.isEmpty())
        assertEquals(1, viewModel.conditions.value.size)
        assertEquals("Harried", viewModel.conditions.value[0].name)
    }
}
