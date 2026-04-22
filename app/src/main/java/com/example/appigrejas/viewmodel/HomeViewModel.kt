package com.example.appigrejas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appigrejas.data.remote.ChurchResponse
import com.example.appigrejas.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ChurchResponse?>(null)
    val uiState: StateFlow<ChurchResponse?> = _uiState

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllContent()
                _uiState.value = response
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching home data", e)
            }
        }
    }
}
