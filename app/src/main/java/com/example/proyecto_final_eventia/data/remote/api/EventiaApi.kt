package com.example.proyecto_final_eventia.data.remote.api

import com.example.proyecto_final_eventia.data.remote.dto.AuthResponseDto
import com.example.proyecto_final_eventia.data.remote.dto.CategoryDto
import com.example.proyecto_final_eventia.data.remote.dto.EventDto
import com.example.proyecto_final_eventia.data.remote.dto.LoginRequestDto
import com.example.proyecto_final_eventia.data.remote.dto.RegisterRequestDto
import com.example.proyecto_final_eventia.data.remote.dto.UserDto
import com.example.proyecto_final_eventia.data.remote.dto.VenueDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventiaApi {

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("events")
    suspend fun getEvents(
        @Query("q") query: String? = null,
        @Query("category") categoryId: Int? = null
    ): List<EventDto>

    @GET("events/{id}")
    suspend fun getEvent(@Path("id") id: Int): EventDto

    @GET("venues")
    suspend fun getVenues(): List<VenueDto>

    @GET("venues/{id}")
    suspend fun getVenue(@Path("id") id: Int): VenueDto

    @GET("venues/{id}/events")
    suspend fun getVenueEvents(@Path("id") venueId: Int): List<EventDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("me")
    suspend fun getMe(): UserDto

    @POST("events/{id}/attendance")
    suspend fun attendEvent(@Path("id") id: Int): EventDto

    @DELETE("events/{id}/attendance")
    suspend fun cancelAttendance(@Path("id") id: Int): EventDto

    @GET("me/attendances")
    suspend fun getMyAttendances(): List<EventDto>
}
