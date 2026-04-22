package com.example.appigrejas.data.remote

import com.example.appigrejas.data.model.BibleVerse
import retrofit2.http.GET
import retrofit2.http.Path

interface BollsBibleApi {
    @GET("get-chapter/{version}/{bookId}/{chapter}/")
    suspend fun getChapter(
        @Path("version") version: String,
        @Path("bookId") bookId: Int,
        @Path("chapter") chapter: Int
    ): List<BibleVerse>
}
