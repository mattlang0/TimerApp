package com.timerapp.service

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.timerapp.TimerApplication

/**
 * VibrateService handles device vibration using the Android Vibrator API. This service abstracts
 * the differences between older and newer Android versions.
 */
object VibrateService {

    /** Vibrates the device for a short duration (500ms). */
    fun vibrate() {
        vibrate(500)
    }

    /**
     * Vibrates the device for a specified duration.
     *
     * @param durationMillis The duration of the vibration in milliseconds
     */
    fun vibrate(durationMillis: Long) {
        val vibrator = getVibrator()

        if (vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android O (API 26) and above, use VibrationEffect
                val vibrationEffect =
                        VibrationEffect.createOneShot(
                                durationMillis,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                vibrator.vibrate(vibrationEffect)
            } else {
                // For older versions, use the deprecated method
                @Suppress("DEPRECATION") vibrator.vibrate(durationMillis)
            }
        }
    }

    /**
     * Gets the Vibrator instance, handling differences between API levels.
     *
     * @return The Vibrator instance or null if not available
     */
    private fun getVibrator(): Vibrator? {
        val context = TimerApplication.getAppContext()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android S (API 31) and above, use VibratorManager
            val vibratorManager =
                    context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as?
                            VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            // For older versions, get Vibrator directly
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
