package com.example.proyecto_final_eventia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyecto_final_eventia.domain.model.Category
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.VenueSummary

@Entity(tableName = "event_cache")
data class EventCacheEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val summary: String,
    val startsAt: String,
    val categoryId: Int,
    val categoryName: String,
    val imageUrl: String?,
    val venueId: Int,
    val venueName: String,
    val venueAddress: String,
    val venueLat: Double?,
    val venueLng: Double?,
    val capacity: Int,
    val availableSpots: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

fun EventCacheEntity.toDomain() = Event(
    id = id,
    title = title,
    summary = summary,
    description = null,
    startsAt = startsAt,
    category = Category(categoryId, categoryName),
    imageUrl = imageUrl,
    venue = VenueSummary(venueId, venueName, venueAddress, venueLat, venueLng),
    capacity = capacity,
    availableSpots = availableSpots,
    isAttending = null
)
