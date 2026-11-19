package com.example.proyecto_final_eventia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_eventia.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Delete
    suspend fun delete(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE eventId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE eventId = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE eventId = :id LIMIT 1")
    suspend fun getById(id: Int): FavoriteEntity?
}
