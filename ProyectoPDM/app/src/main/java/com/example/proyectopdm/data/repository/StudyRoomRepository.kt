package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.StudyRoomDao
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class StudyRoomRepository(private val studyRoomDao: StudyRoomDao) {

    suspend fun syncRooms() {
        try {
            val roomsFromApi = RetrofitClient.studyRoomApiService.getStudyRooms()
            if (roomsFromApi.isNotEmpty()) {
                studyRoomDao.deleteAllRooms()
                studyRoomDao.insertRooms(roomsFromApi)
            }
        } catch (e: Exception) {
            // Manejar error de red o logearlo
            e.printStackTrace()
        }
    }

    fun getAllRooms(): Flow<List<StudyRoom>> {
        return studyRoomDao.getAllRooms()
    }

    suspend fun insertRooms(rooms: List<StudyRoom>) {
        studyRoomDao.insertRooms(rooms)
    }

    suspend fun deleteAllRooms() {
        studyRoomDao.deleteAllRooms()
    }
}