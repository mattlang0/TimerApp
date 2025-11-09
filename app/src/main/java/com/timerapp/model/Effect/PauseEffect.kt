package com.timerapp.model.Effect

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
    override fun execute() {
        // TODO: Implement actual pause logic
        // For now, this would block for 'duration' seconds
        println("Pausing for $duration seconds")
    }
}
