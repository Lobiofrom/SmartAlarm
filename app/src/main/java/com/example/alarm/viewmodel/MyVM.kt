package com.example.alarm.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MyVM : ViewModel() {
    var locationLat: Double? by mutableStateOf(null)
        private set

    var locationLong: Double? by mutableStateOf(null)
        private set

    var workId: UUID? by mutableStateOf(null)
        private set

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            locationLat = latitude
            locationLong = longitude
        }
    }

    fun updateWork(work: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            workId = work
        }
    }
}