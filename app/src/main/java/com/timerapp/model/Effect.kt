package com.timerapp.model

interface Effect<T> {
    fun execute(config: T)
}