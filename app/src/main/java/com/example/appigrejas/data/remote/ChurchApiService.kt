package com.example.appigrejas.data.remote

import com.example.appigrejas.data.model.Sermon
import com.example.appigrejas.data.model.Ministry
import com.example.appigrejas.data.model.LeaderMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ChurchResponse(
    @Json(name = "banners") val banners: List<BannerResponse> = emptyList(),
    @Json(name = "noticias") val noticias: List<NewsResponse> = emptyList(),
    @Json(name = "eventos") val eventos: List<EventResponse> = emptyList(),
    @Json(name = "sermoes") val sermoes: List<SermonResponse> = emptyList(),
    @Json(name = "ministerios") val ministerios: List<MinistryResponse> = emptyList(),
    @Json(name = "mensagemLider") val mensagemLider: List<LeaderMessageResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class BannerResponse(val Titulo: String, val ImagemUrl: String, val Link: String)

@JsonClass(generateAdapter = true)
data class NewsResponse(val Titulo: String, val Descricao: String, val ImagemUrl: String, val Data: String)

@JsonClass(generateAdapter = true)
data class EventResponse(val Titulo: String, val Data: String, val Horario: String, val Local: String)

@JsonClass(generateAdapter = true)
data class SermonResponse(val Titulo: String, val Pregador: String, val VideoUrl: String, val Data: String)

@JsonClass(generateAdapter = true)
data class MinistryResponse(val Nome: String, val Lider: String, val Descricao: String, val ImagemUrl: String)

@JsonClass(generateAdapter = true)
data class LeaderMessageResponse(val Nome: String, val Cargo: String, val Mensagem: String, val FotoUrl: String, val VideoUrl: String)

@JsonClass(generateAdapter = true)
data class PostResponse(val status: String, val message: String)

interface ChurchApiService {
    @GET("exec")
    suspend fun getAllContent(@Query("action") action: String = "all"): ChurchResponse

    @POST("exec")
    suspend fun submitForm(@Body data: Map<String, String>): PostResponse
}
