package com.example.timerapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.timerapp.model.Segment

class HomeViewModel : ViewModel() {

    private val _segments = MutableLiveData<List<Segment>>().apply {
        // Start with an empty list so users can add their own segments
        value = emptyList()
    }
    val segments: LiveData<List<Segment>> = _segments

    fun addSegment(segment: Segment) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        currentList.add(segment)
        _segments.value = currentList
    }

    fun updateSegment(index: Int, updatedSegment: Segment) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        if (index >= 0 && index < currentList.size) {
            currentList[index] = updatedSegment
            _segments.value = currentList
        }
    }

    fun deleteSegment(index: Int) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        if (index >= 0 && index < currentList.size) {
            currentList.removeAt(index)
            _segments.value = currentList
        }
    }

    fun removeSegment(segment: Segment) {
        val currentList = _segments.value?.toMutableList() ?: mutableListOf()
        currentList.remove(segment)
        _segments.value = currentList
    }
}