package com.example.proyectopdm.data.dao

import androidx.room.*
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.ReservationWithRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservations(reservations: List<Reservation>)

    @Transaction
    @Query("SELECT * FROM reservations WHERE userCarnet = :carnet")
    fun getReservationsWithRoomByUser(carnet: String): Flow<List<ReservationWithRoom>>

    @Query("SELECT * FROM reservations WHERE userCarnet = :carnet")
    fun getReservationsByUser(carnet: String): Flow<List<Reservation>>

    @Query("SELECT COUNT(*) FROM reservations WHERE userCarnet = :carnet AND status NOT IN ('CANCELADA_INASISTENCIA', 'CANCELADA_USUARIO')")
    suspend fun getActiveReservationCount(carnet: String): Int

    @Query("SELECT * FROM reservations WHERE roomId = :roomId AND date = :date AND status NOT IN ('CANCELADA_INASISTENCIA', 'CANCELADA_USUARIO')")
    fun getReservationsForRoomAndDate(roomId: Int, date: String): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE date = :date AND status NOT IN ('CANCELADA_INASISTENCIA', 'CANCELADA_USUARIO')")
    fun getAllActiveReservationsByDate(date: String): Flow<List<Reservation>>

    @Update
    suspend fun updateReservation(reservation: Reservation)

    @Delete
    suspend fun deleteReservation(reservation: Reservation)
}
