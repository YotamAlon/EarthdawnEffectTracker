package com.example.earthdawneffecttracker.model

import java.util.UUID

/**
 * Represents a single timed effect modifier on an attribute or stat.
 *
 * @param id Unique identifier for the effect
 * @param target Name of affected attribute/stat (e.g., "Physical Defense", "Initiative")
 * @param amount Numeric modifier (can be positive or negative)
 * @param roundsRemaining Number of rounds remaining before effect expires
 */
data class Effect(
    val id: String = UUID.randomUUID().toString(),
    val target: String,
    val amount: Int,
    val roundsRemaining: Int,
)

/**
 * Aggregated summary for a specific target stat.
 */
data class AggregatedEffect(
    val target: String,
    val totalAmount: Int,
    val activeCount: Int,
)
