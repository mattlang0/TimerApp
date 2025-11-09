package com.timerapp.model.Trigger

class ManualTrigger(private val onExecute: () -> Unit) : Trigger {
    var delay: Int = 0

    override fun execute() {
        // TODO: Apply delay if needed
        onExecute()
    }
}
