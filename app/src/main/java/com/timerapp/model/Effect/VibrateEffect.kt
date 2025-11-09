package com.timerapp.model.Effect

/**
 * VibrateEffect triggers device vibration. No configuration needed.
 *
 * Usage:
 * ```
 * val vibrateEffect = VibrateEffect()
 * vibrateEffect.execute()
 * ```
 */
class VibrateEffect : Effect {
    override fun execute() {
        // TODO: Implement vibrate logic using Android Vibrator API
        println("Vibrating device")
    }
}
