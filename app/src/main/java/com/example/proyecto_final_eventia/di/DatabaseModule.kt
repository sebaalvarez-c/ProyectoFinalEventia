package com.example.proyecto_final_eventia.di

import android.content.Context
import androidx.room.Room
import com.example.proyecto_final_eventia.data.local.EventiaDatabase
import com.example.proyecto_final_eventia.data.local.dao.EventCacheDao
import com.example.proyecto_final_eventia.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EventiaDatabase =
        Room.databaseBuilder(context, EventiaDatabase::class.java, "eventia.db").build()

    @Provides
    fun provideEventCacheDao(db: EventiaDatabase): EventCacheDao = db.eventCacheDao()

    @Provides
    fun provideFavoriteDao(db: EventiaDatabase): FavoriteDao = db.favoriteDao()
}
