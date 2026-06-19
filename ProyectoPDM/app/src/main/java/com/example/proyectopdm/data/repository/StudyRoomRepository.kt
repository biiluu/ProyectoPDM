package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.StudyRoomDao
import com.example.proyectopdm.data.entities.StudyRoom
import kotlinx.coroutines.flow.Flow

class StudyRoomRepository(private val studyRoomDao: StudyRoomDao) {

    suspend fun insertRooms(rooms: List<StudyRoom>) {
        studyRoomDao.insertRooms(rooms)
    }

    fun getAllRooms(): Flow<List<StudyRoom>> {
        return studyRoomDao.getAllRooms()
    }

    suspend fun deleteAllRooms() {
        studyRoomDao.deleteAllRooms()
    }
}