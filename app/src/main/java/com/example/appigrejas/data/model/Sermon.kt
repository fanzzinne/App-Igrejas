package com.example.appigrejas.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sermon(
    val id: String,
    val title: String,
    val preacher: String,
    val date: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: String
)
