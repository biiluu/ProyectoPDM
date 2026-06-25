package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.User
import com.example.proyectopdm.data.repository.ReservationRepository
import com.example.proyectopdm.data.repository.UserRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val reservationRepository: ReservationRepository
    
    var user by mutableStateOf<User?>(null)
        private set
        
    var reservationCount by mutableIntStateOf(0)
        private set

    var totalHours by mutableDoubleStateOf(0.0)
        private set

    init {
        val db = AppDataBase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        reservationRepository = ReservationRepository(db.reservationDao())
    }

    fun loadUser(carnet: String) {
        viewModelScope.launch {
            // Cargar datos del usuario
            user = userRepository.getUserByCarnet(carnet)
            
            // Observar las reservas de forma reactiva
            reservationRepository.getReservationsByUser(carnet).collectLatest { lista ->
                val activas = lista.filter { it.status != "CANCELADA_USUARIO" && it.status != "CANCELADA_INASISTENCIA" }
                reservationCount = activas.size
                
                // Calcular horas totales
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                var totalMinutos = 0L
                activas.forEach { res ->
                    try {
                        val inicio = LocalTime.parse(res.startTime, formatter)
                        val fin = LocalTime.parse(res.endTime, formatter)
                        totalMinutos += Duration.between(inicio, fin).toMinutes()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                totalHours = totalMinutos / 60.0
            }
        }
    }
}
