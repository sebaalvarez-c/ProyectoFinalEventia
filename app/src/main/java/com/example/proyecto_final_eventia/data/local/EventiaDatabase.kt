package com.example.proyecto_final_eventia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto_final_eventia.data.local.dao.EventCacheDao
import com.example.proyecto_final_eventia.data.local.dao.FavoriteDao
import com.example.proyecto_final_eventia.data.local.entity.EventCacheEntity
import com.example.proyecto_final_eventia.data.local.entity.FavoriteEntity

@Database(
    entities = [EventCacheEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EventiaDatabase : RoomDatabase() {
    abstract fun eventCacheDao(): EventCacheDao
    abstract fun favoriteDao(): FavoriteDao
}
