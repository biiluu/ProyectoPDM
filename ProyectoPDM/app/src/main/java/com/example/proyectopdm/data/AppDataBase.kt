package com.example.proyectopdm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectopdm.data.dao.NotificationDao
import com.example.proyectopdm.data.dao.ReservationDao
import com.example.proyectopdm.data.dao.StudyRoomDao
import com.example.proyectopdm.data.dao.UserDao
import com.example.proyectopdm.data.entities.Notification
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.data.entities.User

@Database(entities = [User::class, StudyRoom::class, Reservation::class, Notification::class], version = 7, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studyRoomDao(): StudyRoomDao
    abstract fun reservationDao(): ReservationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "proyecto_pdm_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
