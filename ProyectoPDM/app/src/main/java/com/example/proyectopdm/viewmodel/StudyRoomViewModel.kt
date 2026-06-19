package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.repository.StudyRoomRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StudyRoomViewModel(application: Application) : AndroidViewModel(application){
    private val studyRoomRepository: StudyRoomRepository

    val rooms: StateFlow<List<StudyRoom>>

    init {
        val studyRoomDao = AppDataBase.getDatabase(application).studyRoomDao()
        studyRoomRepository = StudyRoomRepository(studyRoomDao)

        rooms = studyRoomRepository.getAllRooms()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}