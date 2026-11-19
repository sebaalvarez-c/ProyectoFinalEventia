package com.example.proyecto_final_eventia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyecto_final_eventia.domain.model.Category
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.VenueSummary

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val eventId: Int,
    val title: String,
    val summary: String,
    val startsAt: String,
    val categoryId: Int,
    val categoryName: String,
    val imageUrl: String?,
    val venueId: Int,
    val venueName: String,
    val venueAddress: String,
    val savedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toDomain() = Event(
    id = eventId,
    title = title,
    summary = summary,
    description = null,
    startsAt = startsAt,
    category = Category(categoryId, categoryName),
    imageUrl = imageUrl,
    venue = VenueSummary(venueId, venueName, venueAddress, null, null),
    capacity = 0,
    availableSpots = 0,
    isAttending = null
)

fun Event.toFavoriteEntity() = FavoriteEntity(
    eventId = id,
    title = title,
    summary = summary,
    startsAt = startsAt,
    categoryId = category.id,
    categoryName = category.name,
    imageUrl = imageUrl,
    venueId = venue.id,
    venueName = venue.name,
    venueAddress = venue.address
)
