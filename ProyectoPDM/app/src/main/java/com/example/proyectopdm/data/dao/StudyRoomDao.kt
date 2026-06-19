package com.example.proyectopdm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectopdm.data.entities.StudyRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<StudyRoom>)

    @Query("SELECT * FROM study_rooms")
    fun getAllRooms(): Flow<List<StudyRoom>>

    @Query("DELETE FROM study_rooms")
    suspend fun deleteAllRooms()
}