package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.repository.ReservationRepository
import com.example.proyectopdm.data.repository.StudyRoomRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.proyectopdm.data.entities.StudyRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val reservationRepository: ReservationRepository

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        val db = AppDataBase.getDatabase(application)
        reservationRepository = ReservationRepository(db.reservationDao())
    }

    fun getUserReservationsFlow(carnet: String): Flow<List<Reservation>> {
        return reservationRepository.getReservationsByUser(carnet)
    }

    fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            val canceledReservation = reservation.copy(status = "CANCELADA_USUARIO")
            reservationRepository.updateReservation(canceledReservation)
            successMessage = "Reserva cancelada exitosamente."
        }
    }

    fun makeReservation(
        carnet: String,
        room: StudyRoom,
        date: String,
        startTime: String,
        endTime: String,
        peopleCount: Int
    ) {
        errorMessage = null
        successMessage = null
        isLoading = true

        viewModelScope.launch {
            if (peopleCount < room.minCapacity || peopleCount > room.maxCapacity) {
                errorMessage = "Esta sala permite entre ${room.minCapacity} y ${room.maxCapacity} personas."
                isLoading = false
                return@launch
            }

            try {
                val newReservation = Reservation(
                    userCarnet = carnet,
                    roomId = room.id,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    status = "PENDIENTE"
                )

                reservationRepository.insertReservation(newReservation)
                successMessage = "¡Reserva realizada con éxito!"
            } catch (e: Exception) {
                errorMessage = "Error al procesar la reserva. Intente de nuevo."
            } finally {
                isLoading = false
            }
        }
    }
}