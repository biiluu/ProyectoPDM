package com.example.proyectopdm.data.remote

import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.StudyRoom
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyRoomApiService {
    @GET("study-rooms")
    suspend fun getStudyRooms(): List<StudyRoom>

    @GET("reservations")
    suspend fun getActiveReservationsByDate(
        @Query("date") date: String
    ): List<Reservation>

    @GET("study-rooms/{id}/reservations")
    suspend fun getReservationsForRoom(
        @Path("id") roomId: Int,
        @Query("date") date: String
    ): List<Reservation>

    @GET("users/{carnet}/reservations")
    suspend fun getReservationsByUser(
        @Path("carnet") carnet: String
    ): List<Reservation>

    @POST("reservations")
    suspend fun createReservation(@Body reservation: Reservation): Response<Reservation>

    @PATCH("reservations/{id}/status")
    suspend fun updateReservationStatus(
        @Path("id") id: Int,
        @Query("status") status: String
    ): Response<Unit>
}
