package com.timerapp.model.Trigger

import java.time.LocalDateTime

class DateTimeTrigger(private val onExecute: () -> Unit) : Trigger {
    var isEnabled: Boolean = false
    var scheduledTime: LocalDateTime? = null

    override fun execute() {
        if (isEnabled) {
            onExecute()
        }
    }

    fun enable() {
        isEnabled = true
        // TODO: Implement enable logic (e.g., schedule alarm)
    }

    fun disable() {
        isEnabled = false
        // TODO: Implement disable logic (e.g., cancel alarm)
    }
}
