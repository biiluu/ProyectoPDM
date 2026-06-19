package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class TermViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    var isLoading by mutableStateOf(false)
        private set

    init {
        val userDao = AppDataBase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }

    fun acceptTerms(carnet: String, onComplete: () -> Unit) {
        isLoading = true
        viewModelScope.launch {
            val user = userRepository.getUserByCarnet(carnet)
            if (user != null) {

                val updatedUser = user.copy(hasAcceptedTerms = true)
                userRepository.updateUser(updatedUser)

                isLoading = false
                onComplete()
            } else {
                isLoading = false
            }
        }
    }
}