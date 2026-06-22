package com.example.proyectopdm.data.resources

import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.entities.User

object DummyData {
    val users = listOf(
        User("00056824", "123456", "Javier Salamanca", "Ingeniería en Informática"),
        User("00034124", "123456", "Diego Hurtado", "Ingeniería Informática"),
        User("00042124", "123456", "Bianca Luna", "Ingeniería Informática"),
        User("00183224", "123456", "Ana Ramirez", "Ingeniería en Informática"),
        User("00047224", "123456", "Rafael Rubio", "Ingeniería en Informática")
    )

    val studyRooms = listOf(
        // Primera Planta: 3 salas
        StudyRoom(name = "Sala 1 - Nivel 1", minCapacity = 2, maxCapacity = 8, floor = 1),
        StudyRoom(name = "Sala 2 - Nivel 1", minCapacity = 2, maxCapacity = 8, floor = 1),
        StudyRoom(name = "Sala 3 - Nivel 1", minCapacity = 2, maxCapacity = 8, floor = 1),

    )
}
