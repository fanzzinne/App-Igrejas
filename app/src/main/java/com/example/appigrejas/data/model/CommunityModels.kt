package com.example.appigrejas.data.model

data class Ministry(
    val id: String,
    val name: String,
    val description: String,
    val leader: String,
    val imageUrl: String
)

data class LeaderMessage(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val date: String,
    val videoUrl: String? = null
)
