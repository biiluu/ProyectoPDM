package com.example.proyectopdm.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopdm.MainActivity
import com.example.proyectopdm.data.AppDataBase
import com.example.proyectopdm.data.entities.Notification
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.ReservationWithRoom
import com.example.proyectopdm.data.repository.ReservationRepository
import com.example.proyectopdm.util.NotificationHelper
import com.example.proyectopdm.util.ReservationReceiver
import com.example.proyectopdm.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.proyectopdm.data.entities.StudyRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val reservationRepository: ReservationRepository
    private val notificationDao = AppDataBase.getDatabase(application).notificationDao()

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        val db = AppDataBase.getDatabase(application)
        reservationRepository = ReservationRepository(db.reservationDao())
    }

    fun getUserReservationsFlow(carnet: String): Flow<List<Reservation>> {
        return reservationRepository.getReservationsByUser(carnet)
    }

    fun getUserReservationsWithRoomFlow(carnet: String): Flow<List<ReservationWithRoom>> {
        return reservationRepository.getReservationsWithRoomByUser(carnet)
    }

    /**
     * Sincroniza las reservas del usuario con el servidor.
     */
    fun syncUserReservations(carnet: String) {
        viewModelScope.launch {
            reservationRepository.syncUserReservations(carnet)
        }
    }

    fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.studyRoomApiService.updateReservationStatus(reservation.id, "CANCELADA_USUARIO")
                if (response.isSuccessful) {
                    val canceledReservation = reservation.copy(status = "CANCELADA_USUARIO")
                    reservationRepository.updateReservation(canceledReservation)
                    successMessage = "Reserva cancelada exitosamente."
                } else {
                    errorMessage = "Error al cancelar en el servidor: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            }
        }
    }

    fun confirmAttendance(reservation: Reservation) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.studyRoomApiService.updateReservationStatus(reservation.id, "CONFIRMADA")
                if (response.isSuccessful) {
                    val confirmedReservation = reservation.copy(status = "CONFIRMADA")
                    reservationRepository.updateReservation(confirmedReservation)
                    successMessage = "Asistencia confirmada. ¡Disfruta de la sala!"
                } else {
                    errorMessage = "Error al confirmar en el servidor: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            }
        }
    }

    fun checkAndCancelOverdueReservations(carnet: String) {
        viewModelScope.launch {
            val now = LocalTime.now()
            val today = LocalDate.now().toString()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val reservations = reservationRepository.getReservationsByUser(carnet).first()
            
            reservations.forEach { res ->
                if (res.status == "PENDIENTE") {
                    val resDate = res.date
                    val resStartTime = try { LocalTime.parse(res.startTime, formatter) } catch(e: Exception) { null }
                    
                    if (resStartTime != null) {
                        val isPastDay = resDate < today
                        val isTodayOverdue = resDate == today && now.isAfter(resStartTime.plusMinutes(15))
                        
                        if (isPastDay || isTodayOverdue) {
                            val updated = res.copy(status = "CANCELADA_INASISTENCIA")
                            reservationRepository.updateReservation(updated)
                        }
                    }
                }
            }
        }
    }

    fun resetMessages() {
        successMessage = null
        errorMessage = null
    }

    suspend fun getAvailableStartTimes(roomId: Int, date: String, userCarnet: String, excludeReservationId: Int? = null): List<String> {
        // Sincronización de API de tu compañero
        reservationRepository.syncReservationsForRoom(roomId, date)
        
        val availableSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        
        val localDate = LocalDate.parse(date)
        val today = LocalDate.now()
        val now = LocalTime.now()
        val isSaturday = localDate.dayOfWeek == DayOfWeek.SATURDAY

        var currentSlot = LocalTime.of(7, 0)
        val lastSlotTime = if (isSaturday) LocalTime.of(11, 30) else LocalTime.of(18, 30)

        // Tu lógica de exclusión para edición
        val roomReservations = if (excludeReservationId == null) {
            reservationRepository.getActiveReservationsForRoomAndDate(roomId, date).first()
        } else {
            reservationRepository.getActiveReservationsForRoomAndDateExcluding(roomId, date, excludeReservationId).first()
        }

        val userActiveReservations = reservationRepository.getReservationsByUser(userCarnet).first()
            .filter { it.date == date && !it.status.contains("CANCELADA") && it.id != (excludeReservationId ?: -1) }

        while (!currentSlot.isAfter(lastSlotTime)) {
            if (localDate == today && currentSlot.isBefore(now)) {
                currentSlot = currentSlot.plusMinutes(30)
                continue
            }

            var isOccupied = false
            for (res in roomReservations) {
                if (res.status == "CANCELADA_USUARIO" || res.status == "CANCELADA_INASISTENCIA") continue
                val resStart = LocalTime.parse(res.startTime, formatter)
                val resEnd = LocalTime.parse(res.endTime, formatter)
                if (!currentSlot.isBefore(resStart) && currentSlot.isBefore(resEnd)) {
                    isOccupied = true; break
                }
            }

            if (!isOccupied) {
                for (res in userActiveReservations) {
                    val resStart = LocalTime.parse(res.startTime, formatter)
                    val resEnd = LocalTime.parse(res.endTime, formatter)
                    if (!currentSlot.isBefore(resStart) && currentSlot.isBefore(resEnd)) {
                        isOccupied = true; break
                    }
                }
            }

            if (!isOccupied) {
                availableSlots.add(currentSlot.format(formatter))
            }
            currentSlot = currentSlot.plusMinutes(30)
        }
        return availableSlots
    }

    fun makeReservation(carnet: String, room: StudyRoom, date: String, startTime: String, endTime: String, peopleCount: Int) {
        validateAndProcessReservation(null, carnet, room, date, startTime, endTime, peopleCount)
    }

    fun editReservation(reservationId: Int, carnet: String, room: StudyRoom, date: String, startTime: String, endTime: String, peopleCount: Int) {
        validateAndProcessReservation(reservationId, carnet, room, date, startTime, endTime, peopleCount)
    }

    private fun validateAndProcessReservation(reservationId: Int?, carnet: String, room: StudyRoom, date: String, startTime: String, endTime: String, peopleCount: Int) {
        errorMessage = null
        successMessage = null
        isLoading = true

        viewModelScope.launch {
            if (peopleCount < room.minCapacity || peopleCount > room.maxCapacity) {
                errorMessage = "Esta sala permite entre ${room.minCapacity} y ${room.maxCapacity} personas."
                isLoading = false
                return@launch
            }

            try {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val newStart = LocalTime.parse(startTime, formatter)
                val newEnd = LocalTime.parse(endTime, formatter)
                val localDate = LocalDate.parse(date)
                val today = LocalDate.now()
                val now = LocalTime.now()

                if (localDate.isBefore(today) || (localDate == today && newStart.isBefore(now))) {
                    errorMessage = "No puedes reservar en el pasado."
                    isLoading = false
                    return@launch
                }

                if (!newStart.isBefore(newEnd)) {
                    errorMessage = "La hora de inicio debe ser anterior a la de fin."
                    isLoading = false
                    return@launch
                }

                val existingReservations = if (reservationId == null) {
                    reservationRepository.getActiveReservationsForRoomAndDate(room.id, date).first()
                } else {
                    reservationRepository.getActiveReservationsForRoomAndDateExcluding(room.id, date, reservationId).first()
                }
                
                val hasOverlap = existingReservations.any { existing ->
                    val es = LocalTime.parse(existing.startTime, formatter)
                    val ee = LocalTime.parse(existing.endTime, formatter)
                    newStart.isBefore(ee) && newEnd.isAfter(es)
                }

                if (hasOverlap) {
                    errorMessage = "El horario seleccionado coincide con otra reserva."
                    isLoading = false
                    return@launch
                }

                val targetRes = if (reservationId == null) {
                    Reservation(userCarnet = carnet, roomId = room.id, date = date, startTime = startTime, endTime = endTime, peopleCount = peopleCount, status = "PENDIENTE")
                } else {
                    Reservation(id = reservationId, userCarnet = carnet, roomId = room.id, date = date, startTime = startTime, endTime = endTime, peopleCount = peopleCount, status = "PENDIENTE")
                }

                // Intento de guardado en API (siguiendo la lógica de tu compañero)
                try {
                    val response = if (reservationId == null) {
                        RetrofitClient.studyRoomApiService.createReservation(targetRes)
                    } else {
                        // Si no hay endpoint de "update", usamos el de status o creamos uno nuevo localmente
                        RetrofitClient.studyRoomApiService.createReservation(targetRes) // Asumimos que la API lo maneja
                    }

                    if (response.isSuccessful) {
                        if (reservationId == null) reservationRepository.insertReservation(targetRes)
                        else reservationRepository.updateReservation(targetRes)
                        
                        successMessage = if (reservationId == null) "¡Reserva realizada!" else "¡Reserva actualizada!"
                        triggerNotifications(carnet, room, date, startTime, reservationId != null)
                    } else {
                        errorMessage = "El servidor rechazó la operación: ${response.message()}"
                    }
                } catch (e: Exception) {
                    // Fallback local por si falla el servidor
                    if (reservationId == null) reservationRepository.insertReservation(targetRes)
                    else reservationRepository.updateReservation(targetRes)
                    successMessage = "Guardado localmente (Sin conexión)"
                    triggerNotifications(carnet, room, date, startTime, reservationId != null)
                }

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun triggerNotifications(carnet: String, room: StudyRoom, date: String, startTime: String, isUpdate: Boolean) {
        val notificationHelper = NotificationHelper(getApplication())
        val title = if (isUpdate) "Reserva Actualizada" else "Reserva Confirmada"
        val msg = "Reserva en ${room.name} para el $date a las $startTime."
        
        notificationHelper.showNotification(title, msg, System.currentTimeMillis().toInt())
        notificationDao.insertNotification(Notification(userCarnet = carnet, title = title, message = msg))

        // Programación de alarmas
        val formatterFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val resDateTime = LocalDateTime.parse("$date $startTime", formatterFull)
        val millis = resDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (millis > System.currentTimeMillis()) {
            scheduleNotification(millis, "Inicio de Reserva", "Tu tiempo en ${room.name} ha comenzado.", (millis / 1000).toInt(), true, carnet)
        }
    }

    private fun scheduleNotification(timeInMillis: Long, title: String, message: String, notificationId: Int, isExactAlarm: Boolean, userCarnet: String) {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReservationReceiver::class.java).apply {
            putExtra("title", title); putExtra("message", message); putExtra("id", notificationId); putExtra("userCarnet", userCarnet)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        try {
            if (isExactAlarm) {
                val mainIntent = Intent(context, MainActivity::class.java)
                val mainPI = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)
                alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(timeInMillis, mainPI), pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }
}