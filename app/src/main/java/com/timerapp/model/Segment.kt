package com.timerapp.model

import com.timerapp.model.Effect.Effect
import com.timerapp.model.Trigger.DateTimeTrigger
import com.timerapp.model.Trigger.ManualTrigger
import com.timerapp.model.Trigger.Trigger

/**
 * Segment represents a timed sequence of effects with a trigger. Uses Strategy Pattern with
 * Configuration - stores trigger configuration separately from execution logic.
 *
 * Benefits:
 * - Easy to persist/serialize
 * - Can change trigger configuration without recreating segment
 * - Separates data from behavior
 *
 * Usage:
 * ```
 * val segment = Segment(
 *     name = "Breathing Exercise",
 *     effects = arrayOf(VibrateEffect(), PauseEffect(4)),
 *     triggerConfig = TriggerConfig.Manual(delay = 0)
 * )
 *
 * // Later, change the trigger:
 * segment.updateTriggerConfig(TriggerConfig.DateTime(...))
 *
 * // Execute when ready:
 * val trigger = segment.createTrigger()
 * trigger.execute()
 * ```
 */
class Segment(
        val name: String,
        val effects: Array<Effect> = emptyArray(),
        triggerConfig: TriggerConfig
) {
    // Mutable trigger configuration
    var triggerConfig: TriggerConfig = triggerConfig
        private set

    /** Update the trigger configuration */
    fun updateTriggerConfig(newConfig: TriggerConfig) {
        this.triggerConfig = newConfig
    }

    /**
     * Create a Trigger instance based on current configuration. Call this when you're ready to
     * execute the segment.
     */
    fun createTrigger(): Trigger {
        return when (val config = triggerConfig) {
            is TriggerConfig.Manual ->
                    ManualTrigger({ executeEffects() }).apply { delay = config.delay }
            is TriggerConfig.DateTime ->
                    DateTimeTrigger({ executeEffects() }).apply {
                        isEnabled = config.isEnabled
                        scheduledTime = config.time
                    }
        }
    }

    /**
     * Execute the segment's effects directly. This creates a trigger and executes it immediately.
     */
    fun execute() {
        createTrigger().execute()
    }

    private fun executeEffects() {
        effects.forEach { effect -> effect.execute() }
    }
}
