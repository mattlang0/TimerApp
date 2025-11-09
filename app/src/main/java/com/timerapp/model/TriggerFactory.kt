package com.timerapp.model

import com.timerapp.model.Trigger.DateTimeTrigger
import com.timerapp.model.Trigger.ManualTrigger
import com.timerapp.model.Trigger.Trigger

/**
 * Factory for creating Trigger instances from TriggerConfig. Since triggers are always associated
 * with segments, you typically use Segment.createTrigger() instead of calling this directly.
 *
 * Usage:
 * ```
 * // Usually you'd call segment.createTrigger()
 * val trigger = segment.createTrigger()
 *
 * // Or directly with a config:
 * val config = TriggerConfig.Manual(delay = 5)
 * val trigger = TriggerFactory.create(config) {
 *     println("Executing!")
 * }
 * ```
 */
object TriggerFactory {

    /**
     * Create a trigger from a TriggerConfig.
     *
     * @param config The trigger configuration
     * @param execute The function to execute when trigger fires
     * @return A configured Trigger instance
     */
    fun create(config: TriggerConfig, execute: () -> Unit): Trigger {
        return when (config) {
            is TriggerConfig.Manual -> ManualTrigger(execute).apply { delay = config.delay }
            is TriggerConfig.DateTime ->
                    DateTimeTrigger(execute).apply {
                        isEnabled = config.isEnabled
                        scheduledTime = config.time
                    }
        }
    }
}
