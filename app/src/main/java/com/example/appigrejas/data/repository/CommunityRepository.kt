package com.example.appigrejas.data.repository

import com.example.appigrejas.data.model.LeaderMessage
import com.example.appigrejas.data.model.Ministry
import com.example.appigrejas.data.remote.EventResponse
import com.example.appigrejas.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.util.Log

class CommunityRepository {
    private val apiService = RetrofitClient.apiService

    fun getEvents(): Flow<List<EventResponse>> = flow {
        try {
            val response = apiService.getAllContent()
            val eventsList = if (response.eventos.isNotEmpty()) {
                response.eventos
            } else if (response.agenda.isNotEmpty()) {
                response.agenda
            } else {
                emptyList()
            }

            if (eventsList.isNotEmpty()) {
                emit(eventsList)
            } else {
                emit(getMockEvents())
            }
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error fetching events", e)
            emit(getMockEvents())
        }
    }

    private fun getMockEvents() = listOf(
        EventResponse(Titulo = "Culto da Família", Data = "Domingo", Horario = "19:00", Local = "Sede"),
        EventResponse(Titulo = "Escola Bíblica", Data = "Domingo", Horario = "09:00", Local = "Sede"),
        EventResponse(Titulo = "Células", Data = "Quarta-feira", Horario = "20:00", Local = "Casas"),
        EventResponse(Titulo = "Culto de Jovens", Data = "Sábado", Horario = "19:30", Local = "Anexo")
    )

    fun getMinistries(): Flow<List<Ministry>> = flow {
        try {
            val response = apiService.getAllContent()
            val ministries = response.ministerios.map { 
                Ministry(
                    id = it.Nome,
                    name = it.Nome,
                    description = it.Descricao,
                    leader = it.Lider,
                    imageUrl = it.ImagemUrl
                )
            }
            if (ministries.isNotEmpty()) {
                emit(ministries)
            } else {
                emit(getMockMinistries())
            }
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error fetching ministries", e)
            emit(getMockMinistries())
        }
    }

    private fun getMockMinistries() = listOf(
        Ministry("1", "Ministério de Louvor", "Lidera a igreja em adoração através da música.", "Davi Silva", "https://images.unsplash.com/photo-1510936111840-65e151ad71bb?q=80&w=500"),
        Ministry("2", "Ministério Infantil", "Educação cristã para crianças de 0 a 12 anos.", "Ana Souza", "https://images.unsplash.com/photo-1503454537195-1dcabb73ffb9?q=80&w=500"),
        Ministry("3", "Ação Social", "Auxílio a famílias em situação de vulnerabilidade.", "Carlos Lima", "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=500")
    )

    fun getLeaderMessages(): Flow<List<LeaderMessage>> = flow {
        try {
            val response = apiService.getAllContent()
            val messages = response.mensagemLider.map {
                LeaderMessage(
                    id = it.Nome,
                    title = "Mensagem Pastoral",
                    content = it.Mensagem,
                    author = it.Nome,
                    date = "Hoje",
                    videoUrl = it.VideoUrl
                )
            }
            if (messages.isNotEmpty()) {
                emit(messages)
            } else {
                emit(getMockLeaderMessages())
            }
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error fetching messages", e)
            emit(getMockLeaderMessages())
        }
    }

    private fun getMockLeaderMessages() = listOf(
        LeaderMessage("1", "Palavra de Ânimo", "Deus está no controle de todas as coisas. Tenha fé!", "Pr. João Silva", "22/04/2026"),
        LeaderMessage("2", "Visão 2026", "Nossa igreja está crescendo para a glória de Deus.", "Pr. Maria Santos", "15/04/2026")
    )

    suspend fun submitPrayerRequest(name: String, phone: String, category: String, message: String): Boolean {
        return try {
            val data = mapOf(
                "type" to "prayer",
                "nome" to name,
                "telefone" to phone,
                "categoria" to category,
                "mensagem" to message
            )
            val response = apiService.submitForm(data)
            response.status == "success"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun submitGivingReceipt(name: String, amount: String, date: String): Boolean {
        return try {
            val data = mapOf(
                "type" to "giving",
                "nome" to name,
                "valor" to amount,
                "data" to date
            )
            val response = apiService.submitForm(data)
            response.status == "success"
        } catch (e: Exception) {
            false
        }
    }
}
