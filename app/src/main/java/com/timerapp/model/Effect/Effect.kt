package com.timerapp.model.Effect

interface Effect<T> {
    fun execute(config: T)
}