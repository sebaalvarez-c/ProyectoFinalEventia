package com.example.proyecto_final_eventia.data.remote.dto

import com.example.proyecto_final_eventia.data.local.entity.EventCacheEntity
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.VenueSummary

data class EventDto(
    val id: Int,
    val title: String,
    val summary: String,
    val description: String?,
    val startsAt: String,
    val category: CategoryDto,
    val imageUrl: String?,
    val venue: VenueSummaryDto,
    val capacity: Int,
    val availableSpots: Int,
    val isAttending: Boolean?
)

data class VenueSummaryDto(
    val id: Int,
    val name: String,
    val address: String,
    val lat: Double?,
    val lng: Double?
)

fun EventDto.toDomain() = Event(
    id = id,
    title = title,
    summary = summary,
    description = description,
    startsAt = startsAt,
    category = category.toDomain(),
    imageUrl = imageUrl,
    venue = VenueSummary(
        id = venue.id,
        name = venue.name,
        address = venue.address,
        lat = venue.lat,
        lng = venue.lng
    ),
    capacity = capacity,
    availableSpots = availableSpots,
    isAttending = isAttending
)

fun EventDto.toCacheEntity() = EventCacheEntity(
    id = id,
    title = title,
    summary = summary,
    startsAt = startsAt,
    categoryId = category.id,
    categoryName = category.name,
    imageUrl = imageUrl,
    venueId = venue.id,
    venueName = venue.name,
    venueAddress = venue.address,
    venueLat = venue.lat,
    venueLng = venue.lng,
    capacity = capacity,
    availableSpots = availableSpots
)
