package com.example.appigrejas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appigrejas.data.model.LeaderMessage
import com.example.appigrejas.data.model.Ministry
import com.example.appigrejas.data.remote.EventResponse
import com.example.appigrejas.data.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(private val repository: CommunityRepository = CommunityRepository()) : ViewModel() {
    private val _ministries = MutableStateFlow<List<Ministry>>(emptyList())
    val ministries: StateFlow<List<Ministry>> = _ministries

    private val _messages = MutableStateFlow<List<LeaderMessage>>(emptyList())
    val messages: StateFlow<List<LeaderMessage>> = _messages

    private val _events = MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events

    init {
        loadCommunityData()
    }

    private fun loadCommunityData() {
        viewModelScope.launch {
            repository.getMinistries().collect { _ministries.value = it }
            repository.getLeaderMessages().collect { _messages.value = it }
            repository.getEvents().collect { _events.value = it }
        }
    }

    suspend fun submitPrayerRequest(name: String, phone: String, category: String, message: String): Boolean {
        return repository.submitPrayerRequest(name, phone, category, message)
    }

    suspend fun submitGiving(subtipo: String, nome: String, valor: String): Boolean {
        return repository.submitGiving(subtipo, nome, valor)
    }
}
