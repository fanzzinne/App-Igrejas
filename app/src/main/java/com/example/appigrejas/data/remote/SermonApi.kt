package com.example.appigrejas.data.remote

import com.example.appigrejas.data.model.Sermon
import retrofit2.http.GET

interface SermonApi {
    @GET("sermons")
    suspend fun getSermons(): List<Sermon>
}
