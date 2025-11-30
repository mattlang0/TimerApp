package com.timerapp.model.Effect

import com.timerapp.service.PauseService

/**
 * PauseEffect pauses execution for a specified duration. Each instance stores its own duration
 * configuration.
 *
 * Usage:
 * ```
 * val pauseEffect = PauseEffect(duration = 4) // Pause for 4 seconds
 * pauseEffect.execute()
 * ```
 */
class PauseEffect(val duration: Int) : Effect {
    override suspend fun execute() {
        PauseService.pause(duration)
    }
}
