package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.User
import com.example.proyectopdm.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    var user by mutableStateOf<User?>(null)
        private set

    init {
        val userDao = AppDataBase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun loadUser(carnet: String) {
        viewModelScope.launch {
            user = repository.getUserByCarnet(carnet)
        }
    }
}
