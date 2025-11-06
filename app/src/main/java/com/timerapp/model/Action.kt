package com.timerapp.model

import com.timerapp.model.Effect.Effect

class Action<T>(
    val effect: Effect<T>
) {
    fun execute(config: T) {
        effect.execute(config)
    }
}
