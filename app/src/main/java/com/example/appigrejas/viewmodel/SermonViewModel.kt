package com.example.appigrejas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appigrejas.data.model.Sermon
import com.example.appigrejas.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class SermonViewModel : ViewModel() {
    private val _sermons = MutableStateFlow<List<Sermon>>(emptyList())
    val sermons: StateFlow<List<Sermon>> = _sermons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchSermons()
    }

    private fun fetchSermons() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAllContent()
                val fetchedSermons = response.sermoes.mapIndexed { index, it ->
                    Sermon(
                        id = index.toString(),
                        title = it.Titulo,
                        preacher = it.Pregador,
                        date = it.Data,
                        thumbnailUrl = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=500", // Default thumb
                        videoUrl = it.VideoUrl,
                        duration = "45:00"
                    )
                }
                if (fetchedSermons.isNotEmpty()) {
                    _sermons.value = fetchedSermons
                } else {
                    _sermons.value = getMockSermons()
                }
            } catch (e: Exception) {
                Log.e("SermonViewModel", "Error fetching sermons", e)
                _sermons.value = getMockSermons()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getMockSermons() = listOf(
        Sermon("1", "O Poder da Oração", "Pr. João Silva", "20/04/2026", "https://images.unsplash.com/photo-1510936111840-65e151ad71bb?q=80&w=500", "", "45:00"),
        Sermon("2", "Vivendo em Comunidade", "Pr. Maria Santos", "13/04/2026", "https://images.unsplash.com/photo-1529070538774-1843cb3265df?q=80&w=500", "", "38:00"),
        Sermon("3", "A Graça que nos Alcança", "Pr. Roberto Costa", "06/04/2026", "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=500", "", "52:00")
    )
}
