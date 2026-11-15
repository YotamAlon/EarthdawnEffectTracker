package io.github.yotamalon.earthdawneffecttracker.model

import java.util.UUID

/**
 * Represents an active status condition (e.g., "Harried", "Wound")
 * that has no fixed round duration, can stack, and is cleared manually.
 *
 * @param id Unique identifier for the condition
 * @param name Name of the condition
 * @param stacks Number of stacks / severity count
 */
data class Condition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val stacks: Int = 1,
)
