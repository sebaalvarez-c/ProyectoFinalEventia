package com.example.proyecto_final_eventia.di

import com.example.proyecto_final_eventia.data.local.datastore.SessionDataStore
import com.example.proyecto_final_eventia.data.remote.api.EventiaApi
import com.example.proyecto_final_eventia.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // adb reverse tcp:8000 tcp:8000 tunnels localhost:8000 (emulator) → localhost:8000 (host).
    // For a physical device use the host's LAN IP instead (e.g., 192.168.1.X).
    private const val BASE_URL = "http://localhost:8000/api/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionDataStore: SessionDataStore): AuthInterceptor =
        AuthInterceptor(sessionDataStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideEventiaApi(retrofit: Retrofit): EventiaApi =
        retrofit.create(EventiaApi::class.java)
}
