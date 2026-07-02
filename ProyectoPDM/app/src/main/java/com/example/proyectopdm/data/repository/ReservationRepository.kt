package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.ReservationDao
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.ReservationWithRoom
import kotlinx.coroutines.flow.Flow

class ReservationRepository(private val reservationDao: ReservationDao) {
    suspend fun insertReservation(reservation: Reservation) {
        reservationDao.insertReservation(reservation)
    }

    fun getReservationsByUser(carnet: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsByUser(carnet)
    }

    fun getReservationsWithRoomByUser(carnet: String): Flow<List<ReservationWithRoom>> {
        return reservationDao.getReservationsWithRoomByUser(carnet)
    }

    suspend fun getActiveReservationCount(carnet: String): Int {
        return reservationDao.getActiveReservationCount(carnet)
    }

    fun getActiveReservationsForRoomAndDate(roomId: Int, date: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsForRoomAndDate(roomId, date)
    }

    fun getActiveReservationsForRoomAndDateExcluding(roomId: Int, date: String, excludeId: Int): Flow<List<Reservation>> {
        return reservationDao.getReservationsForRoomAndDateExcluding(roomId, date, excludeId)
    }

    suspend fun updateReservation(reservation: Reservation) {
        reservationDao.updateReservation(reservation)
    }
}
