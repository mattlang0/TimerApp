package com.timerapp.service

import android.util.Log
import kotlinx.coroutines.delay

/**
 * PauseService handles pause/countdown functionality. This service provides a way to pause
 * execution for a specified duration with countdown logging.
 */
object PauseService {
    private const val TAG = "PauseService"

    /**
     * Pauses execution for the specified duration, logging each second of the countdown.
     * @param durationSeconds The duration to pause in seconds
     */
    suspend fun pause(durationSeconds: Int) {
        Log.d(TAG, "pause() called with duration: ${durationSeconds} seconds")

        if (durationSeconds <= 0) {
            Log.w(TAG, "Invalid duration: $durationSeconds, skipping pause")
            return
        }

        for (secondsRemaining in durationSeconds downTo 1) {
            Log.d(TAG, "Countdown: $secondsRemaining seconds remaining")
            delay(1000) // Wait for 1 second
        }

        Log.d(TAG, "Pause completed")
    }
}
