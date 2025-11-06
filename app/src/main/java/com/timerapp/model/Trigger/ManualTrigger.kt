package com.timerapp.model.Trigger

class ManualTrigger(
    override val execute: () -> Unit
) : Trigger {
    var delay: Int = 0
}
