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
            // 1. Validar capacidad
            if (peopleCount < room.minCapacity || peopleCount > room.maxCapacity) {
                errorMessage = "Esta sala permite entre ${room.minCapacity} y ${room.maxCapacity} personas."
                isLoading = false
                return@launch
            }

            try {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val newStart = LocalTime.parse(startTime, formatter)
                val newEnd = LocalTime.parse(endTime, formatter)

                // 2. Validar que la hora de inicio sea antes que la de fin
                if (!newStart.isBefore(newEnd)) {
                    errorMessage = "La hora de inicio debe ser anterior a la hora de fin."
                    isLoading = false
                    return@launch
                }

                // 3. Validar traslape de horarios
                val existingReservations = reservationRepository.getActiveReservationsForRoomAndDate(room.id, date).first()
                
                val hasOverlap = existingReservations.any { existing ->
                    val existingStart = LocalTime.parse(existing.startTime, formatter)
                    val existingEnd = LocalTime.parse(existing.endTime, formatter)
                    
                    // Condición de traslape: (NuevoInicio < ExistenteFin) Y (NuevoFin > ExistenteInicio)
                    newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)
                }

                if (hasOverlap) {
                    errorMessage = "El horario seleccionado coincide con otra reserva existente para esta sala."
                    isLoading = false
                    return@launch
                }

                // 4. Si todo es correcto, insertar
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
                errorMessage = "Error al procesar la reserva: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}