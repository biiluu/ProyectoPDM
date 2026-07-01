package com.example.proyectopdm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectopdm.data.entities.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notifications WHERE userCarnet = :carnet ORDER BY timestamp DESC")
    fun getNotificationsByUser(carnet: String): Flow<List<Notification>>

    @Update
    suspend fun updateNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1 WHERE userCarnet = :carnet")
    suspend fun markAllAsRead(carnet: String)

    @Query("DELETE FROM notifications WHERE userCarnet = :carnet")
    suspend fun clearNotifications(carnet: String)
}
