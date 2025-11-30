package com.timerapp.service

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.timerapp.TimerApplication

/**
 * VibrateService handles device vibration using the Android Vibrator API. This service abstracts
 * the differences between older and newer Android versions.
 */
object VibrateService {
    private const val TAG = "VibrateService"

    /** Vibrates the device for a short duration (500ms). */
    fun vibrate() {
        Log.d(TAG, "vibrate() called with default duration (500ms)")
        vibrate(500)
    }

    /**
     * Vibrates the device for a specified duration.
     *
     * @param durationMillis The duration of the vibration in milliseconds
     */
    fun vibrate(durationMillis: Long) {
        Log.d(TAG, "vibrate() called with duration: ${durationMillis}ms")
        val vibrator = getVibrator()

        if (vibrator?.hasVibrator() == true) {
            Log.d(TAG, "Vibrator available, starting vibration")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android O (API 26) and above, use VibrationEffect
                Log.d(TAG, "Using VibrationEffect API (Android O+)")
                val vibrationEffect =
                        VibrationEffect.createOneShot(
                                durationMillis,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                vibrator.vibrate(vibrationEffect)
            } else {
                // For older versions, use the deprecated method
                Log.d(TAG, "Using deprecated vibrate API (pre-Android O)")
                @Suppress("DEPRECATION") vibrator.vibrate(durationMillis)
            }
            Log.d(TAG, "Vibration command sent successfully")
        } else {
            Log.w(TAG, "Vibrator not available or does not support vibration")
        }
    }

    /**
     * Gets the Vibrator instance, handling differences between API levels.
     *
     * @return The Vibrator instance or null if not available
     */
    private fun getVibrator(): Vibrator? {
        Log.d(TAG, "Getting Vibrator instance for API level ${Build.VERSION.SDK_INT}")
        val context = TimerApplication.getAppContext()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android S (API 31) and above, use VibratorManager
            Log.d(TAG, "Using VibratorManager (Android S+)")
            val vibratorManager =
                    context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as?
                            VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            // For older versions, get Vibrator directly
            Log.d(TAG, "Using direct Vibrator service (pre-Android S)")
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
