package com.example.proyecto_final_eventia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_eventia.data.local.entity.EventCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventCacheDao {
    @Query("SELECT * FROM event_cache ORDER BY startsAt ASC")
    fun getAll(): Flow<List<EventCacheEntity>>

    @Query("SELECT * FROM event_cache ORDER BY startsAt ASC")
    suspend fun getAllOnce(): List<EventCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventCacheEntity>)

    @Query("DELETE FROM event_cache")
    suspend fun clearAll()

    @Query("SELECT MAX(cachedAt) FROM event_cache")
    suspend fun getLastCacheTime(): Long?
}
