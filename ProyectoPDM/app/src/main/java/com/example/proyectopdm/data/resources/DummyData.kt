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
        StudyRoom(name = "Sala 1 Primer Nivel", minCapacity = 4, maxCapacity = 10, floor = 1),
        StudyRoom(name = "Sala 2 Primer Nivel", minCapacity = 4, maxCapacity = 10, floor = 1),
        StudyRoom(name = "Sala 3 Primer Nivel", minCapacity = 4, maxCapacity = 10, floor = 1),
        StudyRoom(name = "Sala 1 Segundo Nivel", minCapacity = 6, maxCapacity = 7, floor = 2),
        StudyRoom(name = "Sala 2 Segundo Nivel", minCapacity = 2, maxCapacity = 4, floor = 2),
        StudyRoom(name = "Sala 3 Segundo Nivel", minCapacity = 2, maxCapacity = 4, floor = 2),
        StudyRoom(name = "Sala de Creatividad", minCapacity = 5, maxCapacity = 10, floor = 3),
        StudyRoom(name = "Sala de Taller Digital", minCapacity = 9, maxCapacity = 10, floor = 3),
        StudyRoom(name = "Sala 1 Tercer Nivel", minCapacity = 2, maxCapacity = 4, floor = 3),
        StudyRoom(name = "Sala 2 Tercer Nivel", minCapacity = 2, maxCapacity = 4, floor = 3),
        StudyRoom(name = "Sala 3 Tercer Nivel", minCapacity = 2, maxCapacity = 4, floor = 3),
        StudyRoom(name = "Sala 4 Tercer Nivel", minCapacity = 2, maxCapacity = 4, floor = 3)
    )


}
