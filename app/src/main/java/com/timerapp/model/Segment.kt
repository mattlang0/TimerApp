package com.timerapp.model

import com.timerapp.model.Trigger.Trigger

data class Segment(
    val name: String,
    val actions: Array<Action<*>> = emptyArray(),
    val trigger: Trigger
)
