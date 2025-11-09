package com.timerapp.model

import com.timerapp.model.Effect.Effect
import java.time.LocalDateTime

/**
 * Builder pattern for creating Segment instances with configurable triggers.
 *
 * Usage example:
 * ```
 * val segment = SegmentBuilder()
 *     .setName("Breathing Exercise")
 *     .addEffect(VibrateEffect())
 *     .addEffect(PauseEffect(4))
 *     .setTriggerType(TriggerType.MANUAL)
 *     .setTriggerDelay(0)
 *     .build()
 *
 * // Or with DateTime trigger:
 * val segment = SegmentBuilder()
 *     .setName("Morning Breathing")
 *     .setEffects(listOf(...))
 *     .setTriggerType(TriggerType.DATETIME)
 *     .setTriggerDateTime(LocalDateTime.of(2025, 11, 8, 8, 0))
 *     .setTriggerEnabled(true)
 *     .build()
 * ```
 */
class SegmentBuilder {
    var name: String = ""
    var effects: MutableList<Effect> = mutableListOf()
    var triggerType: TriggerType = TriggerType.MANUAL
    var triggerDelay: Int = 0
    var triggerDateTime: LocalDateTime? = null
    var triggerEnabled: Boolean = false

    fun setName(name: String): SegmentBuilder {
        this.name = name
        return this
    }

    fun addEffect(effect: Effect): SegmentBuilder {
        this.effects.add(effect)
        return this
    }

    fun setEffects(effects: List<Effect>): SegmentBuilder {
        this.effects = effects.toMutableList()
        return this
    }

    fun setTriggerType(type: TriggerType): SegmentBuilder {
        this.triggerType = type
        return this
    }

    fun setTriggerDelay(delay: Int): SegmentBuilder {
        this.triggerDelay = delay
        return this
    }

    fun setTriggerDateTime(dateTime: LocalDateTime?): SegmentBuilder {
        this.triggerDateTime = dateTime
        return this
    }

    fun setTriggerEnabled(enabled: Boolean): SegmentBuilder {
        this.triggerEnabled = enabled
        return this
    }

    fun build(): Segment {
        val triggerConfig =
                when (triggerType) {
                    TriggerType.MANUAL -> TriggerConfig.Manual(delay = triggerDelay)
                    TriggerType.DATETIME ->
                            TriggerConfig.DateTime(
                                    time = triggerDateTime ?: java.time.LocalDateTime.now(),
                                    isEnabled = triggerEnabled
                            )
                }

        return Segment(name, effects.toTypedArray(), triggerConfig)
    }
}
