package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.repository.ReservationRepository
import com.example.proyectopdm.data.repository.StudyRoomRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class StudyRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val studyRoomRepository: StudyRoomRepository
    private val reservationRepository: ReservationRepository
    private val reservationDao = AppDataBase.getDatabase(application).reservationDao()

    val rooms: StateFlow<List<StudyRoom>>
    
    // Flow de salas que NO tienen una reserva activa en este momento
    val availableNowRooms: StateFlow<List<StudyRoom>>

    init {
        val db = AppDataBase.getDatabase(application)
        studyRoomRepository = StudyRoomRepository(db.studyRoomDao())
        reservationRepository = ReservationRepository(db.reservationDao())

        rooms = studyRoomRepository.getAllRooms()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        val today = LocalDate.now().toString()
        
        availableNowRooms = combine(
            studyRoomRepository.getAllRooms(),
            reservationDao.getAllActiveReservationsByDate(today)
        ) { allRooms: List<StudyRoom>, activeReservations: List<Reservation> ->
            getAvailableRoomsNow(allRooms, activeReservations)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Sincronizar datos con la API al iniciar
        syncWithApi()
    }

    /**
     * Llama a la API para actualizar la base de datos local.
     * Gracias al uso de Flow en Room, la UI se actualizará automáticamente 
     * en cuanto los datos se inserten.
     */
    fun syncWithApi() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            // 1. Obtener salas actualizadas
            studyRoomRepository.syncRooms()
            // 2. Obtener reservas actuales para calcular disponibilidad real
            reservationRepository.syncAllActiveReservations(today)
        }
    }

    private fun getAvailableRoomsNow(allRooms: List<StudyRoom>, reservations: List<Reservation>): List<StudyRoom> {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val occupiedRoomIds = reservations
            .filter {
                try {
                    val start = LocalTime.parse(it.startTime, formatter)
                    val end = LocalTime.parse(it.endTime, formatter)
                    // Una sala está ocupada si la hora actual está entre el inicio y el fin
                    // y la reserva está confirmada o pendiente
                    (it.status == "PENDIENTE" || it.status == "CONFIRMADA") &&
                    now.isAfter(start) && now.isBefore(end)
                } catch (e: Exception) {
                    false
                }
            }
            .map { it.roomId }
            .toSet()

        return allRooms.filter { it.id !in occupiedRoomIds }
    }
}
