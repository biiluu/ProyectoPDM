package com.example.proyectopdm.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.User
import com.example.proyectopdm.data.repository.UserRepository
import com.example.proyectopdm.data.resources.DummyData
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    
    var carne by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        val userDao = AppDataBase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        
        // Sincronización forzada con DummyData
        viewModelScope.launch {
            try {
                Log.d("PDM_DEBUG", "Limpiando base de datos...")
                repository.deleteAllUsers()
                
                Log.d("PDM_DEBUG", "Cargando ${DummyData.users.size} usuarios desde DummyData...")
                DummyData.users.forEach { user ->
                    repository.insertUser(user)
                }
                
                val allUsers = repository.getAllUsers()
                Log.d("PDM_DEBUG", "Sincronización completa. Carnets disponibles: ${allUsers.map { it.carnet }}")
            } catch (e: Exception) {
                Log.e("PDM_DEBUG", "Error en sincronización: ${e.message}")
            }
        }
    }

    fun onLogin(onLoginSuccess: (String) -> Unit) {
        val cleanCarne = carne.trim()
        val cleanPassword = password.trim()

        if (cleanCarne.isBlank() || cleanPassword.isBlank()) {
            errorMessage = "Por favor, complete todos los campos"
            return
        }

        isLoading = true
        viewModelScope.launch {
            Log.d("PDM_DEBUG", "Validando credenciales: Carne='$cleanCarne', Pass='$cleanPassword'")
            val user = repository.login(cleanCarne, cleanPassword)
            isLoading = false
            
            if (user != null) {
                Log.d("PDM_DEBUG", "¡Login exitoso! Bienvenido ${user.name}")
                onLoginSuccess(user.carnet)
            } else {
                Log.e("PDM_DEBUG", "Login fallido: Credenciales no encontradas en DB")
                errorMessage = "Credenciales incorrectas"
            }
        }
    }
}
