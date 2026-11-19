package com.example.proyecto_final_eventia.domain.model

data class VenueSummary(
    val id: Int,
    val name: String,
    val address: String,
    val lat: Double?,
    val lng: Double?
)
