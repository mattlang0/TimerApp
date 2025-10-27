package com.example.timerapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.timerapp.model.Segment

class HomeViewModel : ViewModel() {

    private val _segments = MutableLiveData<List<Segment>>().apply {
        // Start with some sample segments for demonstration
        value = listOf(
            Segment("Morning Routine"),
            Segment("Work Session"),
            Segment("Lunch Break"),
            Segment("Exercise")
        )
    }
    val segments: LiveData<List<Segment>> = _segments

    fun addSegment(segment: Segment) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        currentList.add(segment)
        _segments.value = currentList
    }

    fun removeSegment(segment: Segment) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        currentList.remove(segment)
        _segments.value = currentList
    }
}