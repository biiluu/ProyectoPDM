package com.example.proyectopdm.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_rooms")
data class StudyRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val minCapacity: Int,
    val maxCapacity: Int,
    val floor: Int
)
