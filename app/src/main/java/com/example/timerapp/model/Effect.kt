package com.example.timerapp.model

interface Effect<T> {
    fun execute(config: T)
}