package com.example.proyectopdm.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CARNET = "carnet"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000 // 5 minutos
    }

    fun saveSession(carnet: String) {
        prefs.edit().apply {
            putString(KEY_CARNET, carnet)
            putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            apply()
        }
    }

    fun getSavedCarnet(): String? {
        val carnet = prefs.getString(KEY_CARNET, null)
        val lastActivity = prefs.getLong(KEY_LAST_ACTIVITY, 0L)
        val currentTime = System.currentTimeMillis()

        return if (carnet != null && (currentTime - lastActivity) < SESSION_TIMEOUT_MS) {
            carnet
        } else {
            clearSession()
            null
        }
    }

    fun updateLastActivity() {
        if (prefs.contains(KEY_CARNET)) {
            prefs.edit().putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis()).apply()
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
