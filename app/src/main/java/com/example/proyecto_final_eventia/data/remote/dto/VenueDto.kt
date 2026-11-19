package com.example.proyecto_final_eventia.data.remote.dto

import com.example.proyecto_final_eventia.domain.model.Venue

data class VenueDto(
    val id: Int,
    val name: String,
    val address: String,
    val description: String?,
    val openingHours: String?,
    val imageUrl: String?,
    val lat: Double?,
    val lng: Double?,
    val upcomingEventsCount: Int?
)

fun VenueDto.toDomain() = Venue(
    id = id,
    name = name,
    address = address,
    description = description,
    openingHours = openingHours,
    imageUrl = imageUrl,
    lat = lat,
    lng = lng,
    upcomingEventsCount = upcomingEventsCount
)
