package com.example.proyectopdm.data.repository

import com.example.proyectopdm.data.dao.NotificationDao
import com.example.proyectopdm.data.entities.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsByUser(carnet: String): Flow<List<Notification>> = 
        notificationDao.getNotificationsByUser(carnet)

    suspend fun insertNotification(notification: Notification) = 
        notificationDao.insertNotification(notification)

    suspend fun updateNotification(notification: Notification) = 
        notificationDao.updateNotification(notification)

    suspend fun markAllAsRead(carnet: String) = 
        notificationDao.markAllAsRead(carnet)

    suspend fun clearNotifications(carnet: String) = 
        notificationDao.clearNotifications(carnet)
}
