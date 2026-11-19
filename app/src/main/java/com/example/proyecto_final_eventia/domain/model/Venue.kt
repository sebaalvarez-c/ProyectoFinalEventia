package com.example.proyecto_final_eventia.domain.model

data class Venue(
    val id: Int,
    val name: String,
    val address: String,
    val description: String?,
    val schedule: String?,
    val imageUrl: String?,
    val lat: Double?,
    val lng: Double?,
    val upcomingEventsCount: Int?
)
