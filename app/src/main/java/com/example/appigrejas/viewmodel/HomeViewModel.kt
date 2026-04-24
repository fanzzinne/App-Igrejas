package com.example.appigrejas.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appigrejas.data.remote.ChurchResponse
import com.example.appigrejas.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<ChurchResponse?>(null)
    val uiState: StateFlow<ChurchResponse?> = _uiState

    private val _hasNewMural = MutableStateFlow(false)
    val hasNewMural: StateFlow<Boolean> = _hasNewMural

    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllContent()
                _uiState.value = response
                checkNewMural(response)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching home data", e)
            }
        }
    }

    private fun checkNewMural(response: ChurchResponse) {
        val latestMessage = response.mensagemLider.firstOrNull() ?: return
        val lastSeenId = prefs.getString("last_mural_id", "")
        
        // Se a mensagem mais recente for diferente da última vista
        if (latestMessage.Nome + latestMessage.Mensagem != lastSeenId) {
            _hasNewMural.value = true
        } else {
            _hasNewMural.value = false
        }
    }

    fun markMuralAsSeen() {
        val latestMessage = _uiState.value?.mensagemLider?.firstOrNull() ?: return
        prefs.edit().putString("last_mural_id", latestMessage.Nome + latestMessage.Mensagem).apply()
        _hasNewMural.value = false
    }
}
