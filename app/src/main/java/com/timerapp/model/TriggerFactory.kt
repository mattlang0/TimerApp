package com.timerapp.model

import com.timerapp.model.Trigger.DateTimeTrigger
import com.timerapp.model.Trigger.ManualTrigger
import com.timerapp.model.Trigger.Trigger
import java.time.LocalDateTime

/**
 * Factory for creating Trigger instances. Centralizes trigger creation logic and provides a
 * consistent interface.
 *
 * Usage:
 * ```
 * // Create a manual trigger with a map of config
 * val trigger = TriggerFactory.createTrigger(
 *     type = TriggerType.MANUAL,
 *     config = mapOf("delay" to 5),
 *     execute = { println("Executing!") }
 * )
 *
 * // Create from TriggerConfig
 * val config = TriggerConfig.Manual(delay = 5)
 * val trigger = TriggerFactory.createFromConfig(config) {
 *     println("Executing!")
 * }
 * ```
 */
object TriggerFactory {

    /**
     * Create a trigger from a TriggerType and configuration map.
     *
     * @param type The type of trigger to create
     * @param config Configuration parameters as a map
     * @param execute The function to execute when trigger fires
     * @return A configured Trigger instance
     */
    fun createTrigger(type: TriggerType, config: Map<String, Any?>, execute: () -> Unit): Trigger {
        return when (type) {
            TriggerType.MANUAL -> createManualTrigger(config, execute)
            TriggerType.DATETIME -> createDateTimeTrigger(config, execute)
        }
    }

    /**
     * Create a trigger from a TriggerConfig.
     *
     * @param config The trigger configuration
     * @param execute The function to execute when trigger fires
     * @return A configured Trigger instance
     */
    fun createFromConfig(config: TriggerConfig, execute: () -> Unit): Trigger {
        return when (config) {
            is TriggerConfig.Manual -> ManualTrigger(execute).apply { delay = config.delay }
            is TriggerConfig.DateTime ->
                    DateTimeTrigger(execute).apply {
                        isEnabled = config.isEnabled
                        scheduledTime = config.time
                    }
        }
    }

    /**
     * Create a trigger for a Segment based on its configuration.
     *
     * @param segment The segment to create a trigger for
     * @return A configured Trigger that executes the segment's effects
     */
    fun createForSegment(segment: Segment): Trigger {
        return createFromConfig(segment.triggerConfig) { segment.execute() }
    }

    private fun createManualTrigger(config: Map<String, Any?>, execute: () -> Unit): ManualTrigger {
        return ManualTrigger(execute).apply { delay = config["delay"] as? Int ?: 0 }
    }

    private fun createDateTimeTrigger(
            config: Map<String, Any?>,
            execute: () -> Unit
    ): DateTimeTrigger {
        return DateTimeTrigger(execute).apply {
            isEnabled = config["isEnabled"] as? Boolean ?: false
            scheduledTime = config["scheduledTime"] as? LocalDateTime
        }
    }
}
