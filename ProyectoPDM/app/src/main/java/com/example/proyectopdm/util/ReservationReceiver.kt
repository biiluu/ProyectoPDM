package com.example.proyectopdm.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReservationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Recordatorio de Reserva"
        val message = intent.getStringExtra("message") ?: "Tu reserva está por comenzar."
        val id = intent.getIntExtra("id", 0)

        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, message, id)
    }
}
