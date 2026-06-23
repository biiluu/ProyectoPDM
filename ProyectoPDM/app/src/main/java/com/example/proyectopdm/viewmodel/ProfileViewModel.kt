package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.User
import com.example.proyectopdm.data.repository.ReservationRepository
import com.example.proyectopdm.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val reservationRepository: ReservationRepository
    
    var user by mutableStateOf<User?>(null)
        private set
        
    var reservationCount by mutableIntStateOf(0)
        private set

    init {
        val db = AppDataBase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        reservationRepository = ReservationRepository(db.reservationDao())
    }

    fun loadUser(carnet: String) {
        viewModelScope.launch {
            user = userRepository.getUserByCarnet(carnet)
            reservationCount = reservationRepository.getActiveReservationCount(carnet)
        }
    }
}
