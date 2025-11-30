package com.timerapp.model.Trigger

class ManualTrigger(private val onExecute: suspend () -> Unit) : Trigger {
    var delay: Int = 0

    override suspend fun execute() {
        // TODO: Apply delay if needed
        onExecute()
    }
}
