package com.timerapp.model.Effect

import com.timerapp.service.VibrateService

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
        VibrateService.vibrate()
    }
}
