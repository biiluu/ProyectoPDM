package com.example.proyectopdm.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReservationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Recordatorio de Reserva"
        val message = intent.getStringExtra("message") ?: "Tu reserva está por comenzar."
        val id = intent.getIntExtra("id", 0)
        val carnet = intent.getStringExtra("userCarnet") ?: ""

        // 1. Mostrar la notificación en el sistema
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, message, id)

        // 2. Guardar en el historial de la base de datos
        if (carnet.isNotEmpty()) {
            val database = AppDataBase.getDatabase(context)
            val notificationDao = database.notificationDao()
            
            CoroutineScope(Dispatchers.IO).launch {
                notificationDao.insertNotification(
                    Notification(
                        userCarnet = carnet,
                        title = title,
                        message = message
                    )
                )
            }
        }
    }
}
