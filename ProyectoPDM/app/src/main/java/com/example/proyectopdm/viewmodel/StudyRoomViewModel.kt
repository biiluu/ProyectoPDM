package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.repository.StudyRoomRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class StudyRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val studyRoomRepository: StudyRoomRepository
    private val reservationDao = AppDataBase.getDatabase(application).reservationDao()

    val rooms: StateFlow<List<StudyRoom>>
    
    // Flow de salas que NO tienen una reserva activa en este momento
    val availableNowRooms: StateFlow<List<StudyRoom>>

    init {
        val studyRoomDao = AppDataBase.getDatabase(application).studyRoomDao()
        studyRoomRepository = StudyRoomRepository(studyRoomDao)

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
