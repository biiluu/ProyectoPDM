package com.example.proyectopdm.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["carnet"],
            childColumns = ["userCarnet"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudyRoom::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userCarnet: String,
    val roomId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String
)
