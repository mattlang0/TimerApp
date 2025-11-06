package com.timerapp.model.Trigger

class DateTimeTrigger(
    override val execute: () -> Unit
) : Trigger {
    var isEnabled: Boolean = false

    fun enable() {
        // TODO: Implement enable logic
    }

    fun disable() {
        // TODO: Implement disable logic
    }
}
