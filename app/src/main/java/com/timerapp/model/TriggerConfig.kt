package com.timerapp.model

import java.time.LocalDateTime

/**
 * Configuration for triggers - separates data from behavior. This can be easily
 * serialized/persisted to database or SharedPreferences.
 *
 * Usage:
 * ```
 * // Manual trigger
 * val config = TriggerConfig.Manual(delay = 5)
 *
 * // DateTime trigger
 * val config = TriggerConfig.DateTime(
 *     time = LocalDateTime.of(2025, 11, 8, 8, 0),
 *     isEnabled = true
 * )
 * ```
 */
sealed class TriggerConfig {
    /**
     * Manual trigger - executes immediately when called
     * @param delay Delay in seconds before execution
     */
    data class Manual(val delay: Int = 0) : TriggerConfig()

    /**
     * DateTime trigger - executes at a specific date/time
     * @param time The scheduled date/time for execution
     * @param isEnabled Whether the trigger is enabled
     */
    data class DateTime(val time: LocalDateTime, val isEnabled: Boolean = false) : TriggerConfig()
}
