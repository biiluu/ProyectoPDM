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
        // Segunda Planta: 3 salas y 8 cubículos individuales
                StudyRoom(name = "Sala 1 - Nivel 2", minCapacity = 2, maxCapacity = 6, floor = 2),
                StudyRoom(name = "Sala 2 - Nivel 2", minCapacity = 2, maxCapacity = 6, floor = 2),
                StudyRoom(name = "Sala 3 - Nivel 2", minCapacity = 2, maxCapacity = 6, floor = 2),
                StudyRoom(name = "Cubículo Individual 1", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 2", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 3", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 4", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 5", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 6", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 7", minCapacity = 1, maxCapacity = 1, floor = 2),
                StudyRoom(name = "Cubículo Individual 8", minCapacity = 1, maxCapacity = 1, floor = 2),

                // Tercera Planta: 4 salas, una sala recreativa y una sala de taller digital
                StudyRoom(name = "Sala 1 - Nivel 3", minCapacity = 2, maxCapacity = 4, floor = 3),
                StudyRoom(name = "Sala 2 - Nivel 3", minCapacity = 2, maxCapacity = 4, floor = 3),
                StudyRoom(name = "Sala 3 - Nivel 3", minCapacity = 2, maxCapacity = 4, floor = 3),
                StudyRoom(name = "Sala 4 - Nivel 3", minCapacity = 2, maxCapacity = 4, floor = 3),
                StudyRoom(name = "Sala Recreativa", minCapacity = 1, maxCapacity = 15, floor = 3),
                StudyRoom(name = "Sala de Taller Digital", minCapacity = 1, maxCapacity = 12, floor = 3)
            )
}
