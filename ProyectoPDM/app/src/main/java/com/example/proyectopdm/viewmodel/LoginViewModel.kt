package com.example.proyectopdm.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.repository.StudyRoomRepository
import com.example.proyectopdm.data.repository.UserRepository
import com.example.proyectopdm.data.resources.DummyData
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val studyRoomRepository: StudyRoomRepository
    
    var carne by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        val db = AppDataBase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        studyRoomRepository = StudyRoomRepository(db.studyRoomDao())
        
        // Sincronización forzada con DummyData
        viewModelScope.launch {
            try {
                Log.d("PDM_DEBUG", "Limpiando base de datos...")
                userRepository.deleteAllUsers()
                studyRoomRepository.deleteAllRooms()
                
                Log.d("PDM_DEBUG", "Cargando datos desde DummyData...")
                DummyData.users.forEach { user ->
                    userRepository.insertUser(user)
                }
                
                studyRoomRepository.insertRooms(DummyData.studyRooms)
                
                Log.d("PDM_DEBUG", "Sincronización completa.")
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
            val user = userRepository.login(cleanCarne, cleanPassword)
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
