package com.example.proyectopdm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectopdm.data.entities.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Insert
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE userCarnet = :carnet")
    fun getReservationsByUser(carnet: String): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE roomId = :roomId AND date = :date AND status NOT IN ('CANCELADA_INASISTENCIA', 'CANCELADA_USUARIO')")
    fun getReservationsForRoomAndDate(roomId: Int, date: String): Flow<List<Reservation>>

    @Update
    suspend fun updateReservation(reservation: Reservation)
}