package com.example.appigrejas.util

import android.net.Uri

object VideoUtils {
    fun getEmbedUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null

        // Google Drive
        if (url.contains("drive.google.com")) {
            val fileId = if (url.contains("/d/")) {
                url.split("/d/").getOrNull(1)?.split("/")?.firstOrNull()?.split("?")?.firstOrNull()
            } else if (url.contains("id=")) {
                Uri.parse(url).getQueryParameter("id")
            } else null

            return fileId?.let { "https://drive.google.com/file/d/$it/preview" }
        }

        // YouTube
        if (url.contains("youtube.com/embed/")) return url

        val videoId = when {
            url.contains("youtu.be/") -> {
                url.split("youtu.be/").getOrNull(1)?.split("/")?.firstOrNull()?.split("?")?.firstOrNull()
            }
            url.contains("youtube.com/watch") -> {
                Uri.parse(url).getQueryParameter("v")
            }
            url.contains("youtube.com/shorts/") -> {
                url.split("shorts/").getOrNull(1)?.split("/")?.firstOrNull()?.split("?")?.firstOrNull()
            }
            else -> null
        }

        return videoId?.let { "https://www.youtube.com/embed/$it" }
    }
}
