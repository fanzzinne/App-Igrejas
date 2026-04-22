package com.example.appigrejas.data.model

data class BibleBook(
    val id: Int,
    val name: String,
    val abbrev: String,
    val chapters: Int
)

data class BibleVerse(
    val verse: Int,
    val text: String
)
