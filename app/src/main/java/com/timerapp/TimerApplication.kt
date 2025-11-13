package com.timerapp

import android.app.Application
import android.content.Context

/**
 * Custom Application class for the Timer app. Provides global access to the application context.
 */
class TimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: TimerApplication

        /** Get the application context. This can be called from anywhere in the app. */
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}
