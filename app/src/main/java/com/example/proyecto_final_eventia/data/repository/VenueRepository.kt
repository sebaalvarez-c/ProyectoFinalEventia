package com.example.proyecto_final_eventia.data.repository

import com.example.proyecto_final_eventia.data.remote.api.EventiaApi
import com.example.proyecto_final_eventia.data.remote.dto.toDomain
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.Venue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VenueRepository @Inject constructor(
    private val api: EventiaApi
) {

    suspend fun getVenues(): Result<List<Venue>> = runCatching {
        api.getVenues().map { it.toDomain() }
    }

    suspend fun getVenueById(id: Int): Result<Venue> = runCatching {
        api.getVenue(id).toDomain()
    }

    suspend fun getVenueEvents(venueId: Int): Result<List<Event>> = runCatching {
        api.getVenueEvents(venueId).map { it.toDomain() }
    }
}
