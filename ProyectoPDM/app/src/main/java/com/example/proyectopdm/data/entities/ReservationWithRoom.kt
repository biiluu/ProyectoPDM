package com.example.proyectopdm.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ReservationWithRoom(
    @Embedded val reservation: Reservation,
    @Relation(
        parentColumn = "roomId",
        entityColumn = "id"
    )
    val studyRoom: StudyRoom
)
