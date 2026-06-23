package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.ReservationDao
import com.example.proyectopdm.data.entities.Reservation
import kotlinx.coroutines.flow.Flow

class ReservationRepository(private val reservationDao: ReservationDao) {
    suspend fun insertReservation(reservation: Reservation) {
        reservationDao.insertReservation(reservation)
    }

    fun getReservationsByUser(carnet: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsByUser(carnet)
    }

    suspend fun getActiveReservationCount(carnet: String): Int {
        return reservationDao.getActiveReservationCount(carnet)
    }

    fun getActiveReservationsForRoomAndDate(roomId: Int, date: String): Flow<List<Reservation>> {
        return reservationDao.getReservationsForRoomAndDate(roomId, date)
    }

    suspend fun updateReservation(reservation: Reservation) {
        reservationDao.updateReservation(reservation)
    }
}