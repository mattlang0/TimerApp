package com.timerapp.model

import com.timerapp.model.Trigger.Trigger

class Segment(
        val name: String,
        val actions: Array<Action<*>> = emptyArray(),
        createTrigger: (() -> Unit) -> Trigger
) {
    val trigger: Trigger = createTrigger { executeActions() }

    private fun executeActions() {
        actions.forEach { action ->
            @Suppress("UNCHECKED_CAST") (action as Action<Any?>).execute(null)
        }
    }
}
