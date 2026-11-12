package com.example.earthdawneffecttracker

import com.example.earthdawneffecttracker.viewmodel.EffectViewModel
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

        assertEquals(1, viewModel.currentRound.value)

        // Finish round 1
        val expiredRound1 = viewModel.finishRound()
        assertEquals(1, expiredRound1) // 1 expired (Physical Defense)
        assertEquals(2, viewModel.currentRound.value)
        assertEquals(1, viewModel.effects.value.size)
        assertEquals("Initiative", viewModel.effects.value[0].target)
        assertEquals(1, viewModel.effects.value[0].roundsRemaining)

        // Finish round 2
        val expiredRound2 = viewModel.finishRound()
        assertEquals(1, expiredRound2) // 1 expired (Initiative)
        assertEquals(3, viewModel.currentRound.value)
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
    fun `test resetAll clears everything and resets round counter`() {
        viewModel.addEffect("Physical Defense", 2, 3)
        viewModel.addEffect("Initiative", -1, 4)
        viewModel.finishRound() // Round is now 2

        assertEquals(2, viewModel.currentRound.value)
        assertEquals(2, viewModel.effects.value.size)

        viewModel.resetAll()

        assertEquals(1, viewModel.currentRound.value)
        assertTrue(viewModel.effects.value.isEmpty())
        assertTrue(viewModel.aggregatedEffects.value.isEmpty())
    }
}
