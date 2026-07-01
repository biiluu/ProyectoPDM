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
     * Cancela una reserva a petición del usuario.
     */
    fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            val canceledReservation = reservation.copy(status = "CANCELADA_USUARIO")
            reservationRepository.updateReservation(canceledReservation)
            successMessage = "Reserva cancelada exitosamente."
        }
    }

    /**
     * Confirma la asistencia del usuario a la sala (Check-in).
     */
    fun confirmAttendance(reservation: Reservation) {
        viewModelScope.launch {
            val confirmedReservation = reservation.copy(status = "CONFIRMADA")
            reservationRepository.updateReservation(confirmedReservation)
            successMessage = "Asistencia confirmada. ¡Disfruta de la sala!"
        }
    }

    /**
     * Revisa y cancela automáticamente las reservas que no registraron asistencia
     * después de 15 minutos de la hora de inicio.
     */
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

    suspend fun getAvailableStartTimes(roomId: Int, date: String, userCarnet: String): List<String> {
        val availableSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        
        val localDate = LocalDate.parse(date)
        val today = LocalDate.now()
        val now = LocalTime.now()
        val isSaturday = localDate.dayOfWeek == DayOfWeek.SATURDAY

        var currentSlot = LocalTime.of(7, 0)
        val lastSlotTime = if (isSaturday) LocalTime.of(11, 30) else LocalTime.of(18, 30)

        val roomReservations = reservationRepository.getActiveReservationsForRoomAndDate(roomId, date).first()
        val userActiveReservations = reservationRepository.getReservationsByUser(userCarnet).first()
            .filter { it.date == date && !it.status.contains("CANCELADA") }

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

    fun makeReservation(
        carnet: String,
        room: StudyRoom,
        date: String,
        startTime: String,
        endTime: String,
        peopleCount: Int
    ) {
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

                if (localDate.isBefore(today)) {
                    errorMessage = "No puedes reservar en una fecha pasada."
                    isLoading = false
                    return@launch
                }

                if (localDate == today && newStart.isBefore(now)) {
                    errorMessage = "No puedes reservar un horario que ya pasó."
                    isLoading = false
                    return@launch
                }

                if (!newStart.isBefore(newEnd)) {
                    errorMessage = "La hora de inicio debe ser anterior a la hora de fin."
                    isLoading = false
                    return@launch
                }
                
                when (localDate.dayOfWeek) {
                    DayOfWeek.SATURDAY -> {
                        if (newEnd.isAfter(LocalTime.of(12, 0))) {
                            errorMessage = "Los sábados el servicio es hasta las 12:00 PM."
                            isLoading = false
                            return@launch
                        }
                    }
                    DayOfWeek.SUNDAY -> {
                        errorMessage = "Los domingos no hay servicio de reserva."
                        isLoading = false
                        return@launch
                    }
                    else -> {
                        if (newEnd.isAfter(LocalTime.of(19, 0))) {
                            errorMessage = "De lunes a viernes el servicio es hasta las 7:00 PM."
                            isLoading = false
                            return@launch
                        }
                    }
                }

                val existingReservations = reservationRepository.getActiveReservationsForRoomAndDate(room.id, date).first()
                
                val hasOverlap = existingReservations.any { existing ->
                    val existingStart = LocalTime.parse(existing.startTime, formatter)
                    val existingEnd = LocalTime.parse(existing.endTime, formatter)
                    newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)
                }

                if (hasOverlap) {
                    errorMessage = "El horario seleccionado coincide con otra reserva existente."
                    isLoading = false
                    return@launch
                }

                val newReservation = Reservation(
                    userCarnet = carnet,
                    roomId = room.id,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    status = "PENDIENTE"
                )

                reservationRepository.insertReservation(newReservation)
                successMessage = "¡Reserva realizada con éxito!"

                // --- SISTEMA DE NOTIFICACIONES ---
                val notificationHelper = NotificationHelper(getApplication())
                val notificationTitle = "Reserva Confirmada"
                val notificationMessage = "Has reservado ${room.name} para el $date a las $startTime."
                
                // 1. Notificación inmediata de confirmación
                notificationHelper.showNotification(
                    notificationTitle,
                    notificationMessage,
                    System.currentTimeMillis().toInt()
                )

                // 2. Guardar en el historial de la DB (Notificación Inmediata)
                notificationDao.insertNotification(
                    Notification(
                        userCarnet = carnet,
                        title = notificationTitle,
                        message = notificationMessage
                    )
                )

                // 3. Programar notificaciones futuras
                val formatterFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val resDateTime = LocalDateTime.parse("$date $startTime", formatterFull)
                val millis = resDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Notificación 1 hora antes
                val oneHourBefore = millis - (60 * 60 * 1000)
                if (oneHourBefore > System.currentTimeMillis()) {
                    scheduleNotification(
                        oneHourBefore,
                        "Recordatorio de Reserva",
                        "Tu reserva en ${room.name} inicia en 1 hora.",
                        (millis / 1000).toInt() + 1,
                        isExactAlarm = false,
                        userCarnet = carnet
                    )
                }

                // Notificación al momento de inicio (Usa setAlarmClock para máxima precisión)
                if (millis > System.currentTimeMillis()) {
                    scheduleNotification(
                        millis,
                        "Inicio de Reserva",
                        "Tu tiempo en ${room.name} ha comenzado. ¡No olvides hacer check-in!",
                        (millis / 1000).toInt() + 2,
                        isExactAlarm = true,
                        userCarnet = carnet
                    )
                }

            } catch (e: Exception) {
                errorMessage = "Error al procesar la reserva: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun scheduleNotification(timeInMillis: Long, title: String, message: String, notificationId: Int, isExactAlarm: Boolean, userCarnet: String) {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReservationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("id", notificationId)
            putExtra("userCarnet", userCarnet)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (isExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // setAlarmClock es lo más fiable para despertar al sistema
                val mainActivityIntent = Intent(context, MainActivity::class.java)
                val mainPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
                val alarmClockInfo = AlarmManager.AlarmClockInfo(timeInMillis, mainPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                Log.d("ReservationVM", "Alarma programada como Reloj para: $timeInMillis")
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            }
        } catch (e: SecurityException) {
            Log.e("ReservationVM", "Error de seguridad al programar alarma: ${e.message}")
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }
}
