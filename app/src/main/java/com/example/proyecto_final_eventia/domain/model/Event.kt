package com.example.proyecto_final_eventia.domain.model

data class Event(
    val id: Int,
    val title: String,
    val summary: String,
    val description: String?,
    val startsAt: String,
    val category: Category,
    val imageUrl: String?,
    val venue: VenueSummary,
    val capacity: Int,
    val availableSpots: Int,
    val isAttending: Boolean?
)
