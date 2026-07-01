package com.example.proyectopdm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Notification
import com.example.proyectopdm.data.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository
    
    init {
        val database = AppDataBase.getDatabase(application)
        repository = NotificationRepository(database.notificationDao())
    }

    fun getNotifications(carnet: String): Flow<List<Notification>> {
        return repository.getNotificationsByUser(carnet)
    }

    fun markAsRead(notification: Notification) {
        viewModelScope.launch {
            repository.updateNotification(notification.copy(isRead = true))
        }
    }

    fun markAllAsRead(carnet: String) {
        viewModelScope.launch {
            repository.markAllAsRead(carnet)
        }
    }

    fun clearAll(carnet: String) {
        viewModelScope.launch {
            repository.clearNotifications(carnet)
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            repository.insertNotification(notification)
        }
    }
}
