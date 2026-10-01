package com.example.data.model

data class RadioStation(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val reciterName: String = "",
    val badge: String = "مباشر 🔴"
)
