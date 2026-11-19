package com.example.proyecto_final_eventia.data.repository

import com.example.proyecto_final_eventia.data.local.dao.EventCacheDao
import com.example.proyecto_final_eventia.data.local.dao.FavoriteDao
import com.example.proyecto_final_eventia.data.local.entity.toDomain
import com.example.proyecto_final_eventia.data.local.entity.toFavoriteEntity
import com.example.proyecto_final_eventia.data.remote.api.EventiaApi
import com.example.proyecto_final_eventia.data.remote.dto.toCacheEntity
import com.example.proyecto_final_eventia.data.remote.dto.toDomain
import com.example.proyecto_final_eventia.domain.model.Category
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class EventsResult(
    val events: List<Event>,
    val isOffline: Boolean = false,
    val cachedAt: Long? = null
)

@Singleton
class EventRepository @Inject constructor(
    private val api: EventiaApi,
    private val eventCacheDao: EventCacheDao,
    private val favoriteDao: FavoriteDao,
    private val networkMonitor: NetworkMonitor
) {

    fun getEvents(query: String? = null, categoryId: Int? = null): Flow<EventsResult> = flow {
        if (networkMonitor.isOnline()) {
            try {
                val dtos = api.getEvents(query, categoryId)
                if (query == null && categoryId == null) {
                    eventCacheDao.clearAll()
                    eventCacheDao.insertAll(dtos.map { it.toCacheEntity() })
                }
                emit(EventsResult(events = dtos.map { it.toDomain() }, isOffline = false))
            } catch (e: Exception) {
                val cached = eventCacheDao.getAllOnce()
                val cachedAt = eventCacheDao.getLastCacheTime()
                if (cached.isNotEmpty()) {
                    val filtered = filterLocally(cached.map { it.toDomain() }, query, categoryId)
                    emit(EventsResult(events = filtered, isOffline = true, cachedAt = cachedAt))
                } else {
                    throw e
                }
            }
        } else {
            val cached = eventCacheDao.getAllOnce()
            val cachedAt = eventCacheDao.getLastCacheTime()
            val filtered = filterLocally(cached.map { it.toDomain() }, query, categoryId)
            emit(EventsResult(events = filtered, isOffline = true, cachedAt = cachedAt))
        }
    }

    private fun filterLocally(
        events: List<Event>,
        query: String?,
        categoryId: Int?
    ): List<Event> {
        return events.filter { event ->
            val matchesQuery = query.isNullOrBlank() ||
                event.title.contains(query, ignoreCase = true)
            val matchesCategory = categoryId == null || event.category.id == categoryId
            matchesQuery && matchesCategory
        }
    }

    suspend fun getEventById(id: Int): Result<Event> = runCatching {
        api.getEvent(id).toDomain()
    }

    suspend fun attendEvent(id: Int): Result<Event> = runCatching {
        api.attendEvent(id).toDomain()
    }

    suspend fun cancelAttendance(id: Int): Result<Event> = runCatching {
        api.cancelAttendance(id).toDomain()
    }

    suspend fun getMyAttendances(): Result<List<Event>> = runCatching {
        api.getMyAttendances().map { it.toDomain() }
    }

    suspend fun getCategories(): Result<List<Category>> = runCatching {
        api.getCategories().map { it.toDomain() }
    }

    fun getFavorites(): Flow<List<Event>> =
        favoriteDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(event: Event) {
        val existing = favoriteDao.getById(event.id)
        if (existing != null) {
            favoriteDao.deleteById(event.id)
        } else {
            favoriteDao.insert(event.toFavoriteEntity())
        }
    }

    fun isFavorite(eventId: Int): Flow<Boolean> = favoriteDao.isFavorite(eventId)
}
