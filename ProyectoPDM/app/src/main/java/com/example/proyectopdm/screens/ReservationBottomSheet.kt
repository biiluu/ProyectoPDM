package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.viewmodel.ReservationViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationBottomSheet(
    room: StudyRoom,
    carnet: String,
    reservationViewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    editingReservation: Reservation? = null // Nuevo parámetro para edición
) {
    val UcaBlue = Color(0xFF1D3354)

    // Inicializar estados con los valores de la reserva si estamos editando
    var selectedPeople by remember { 
        mutableIntStateOf(editingReservation?.let { 2 } ?: room.minCapacity) // Nota: La entidad Reservation no guarda peopleCount, asumiendo 2 por defecto o deberia agregarse a la entidad
    }
    
    // Como la entidad Reservation no tiene peopleCount, vamos a dejarlo en minCapacity por ahora
    // o el usuario puede ajustarlo. Si quisieramos persistirlo habria que cambiar la tabla.
    
    var expandedPeople by remember { mutableStateOf(false) }
    val peopleOptions = (room.minCapacity..room.maxCapacity).toList()

    var selectedDate by remember {
        mutableStateOf(
            editingReservation?.let { LocalDate.parse(it.date) } ?:
            if (LocalDate.now().dayOfWeek == DayOfWeek.SUNDAY) LocalDate.now().plusDays(1)
            else LocalDate.now()
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

    var availableTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTime by remember { mutableStateOf(editingReservation?.startTime ?: "") }
    var expandedTime by remember { mutableStateOf(false) }

    // Calcular duración inicial si estamos editando
    val initialDuration = editingReservation?.let {
        val start = LocalTime.parse(it.startTime)
        val end = LocalTime.parse(it.endTime)
        val diffMinutes = java.time.Duration.between(start, end).toMinutes()
        diffMinutes / 60.0
    } ?: 1.0

    val durationOptions = listOf(0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0)
    var selectedDuration by remember { mutableDoubleStateOf(initialDuration) }
    var expandedDuration by remember { mutableStateOf(false) }

    fun formatDuration(d: Double): String {
        if (d == 0.5) return "30 minutos"
        val intPart = d.toInt()
        val isHalf = d - intPart > 0
        val numStr = if (isHalf) d.toString() else intPart.toString()
        return if (d == 1.0) "1 hora" else "$numStr horas"
    }

    LaunchedEffect(selectedDate) {
        val dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        // Al editar, permitimos que la hora actual de la reserva aparezca en la lista
        availableTimes = reservationViewModel.getAvailableStartTimes(
            room.id, 
            dateString, 
            carnet, 
            excludeReservationId = editingReservation?.id
        )
        
        // Si el tiempo seleccionado no está en las nuevas horas disponibles (porque ya pasó o cambió el día), lo reseteamos
        // A menos que sea el tiempo original de la reserva que estamos editando
        if (selectedTime.isNotEmpty() && !availableTimes.contains(selectedTime) && selectedTime != editingReservation?.startTime) {
            selectedTime = ""
        }
        
        // Si estamos editando y el tiempo original es válido para el nuevo día, asegurarnos que esté en la lista
        if (editingReservation != null && selectedDate.toString() == editingReservation.date && !availableTimes.contains(editingReservation.startTime)) {
             // Esto puede pasar si el horario de la reserva ya pasó. 
             // En edición, tal vez queramos permitir mantenerlo si no se cambia nada más, 
             // pero las reglas de negocio dicen que no se puede reservar en el pasado.
        }
    }

    LaunchedEffect(Unit) {
        reservationViewModel.resetMessages()
    }

    var previousSuccess by remember { mutableStateOf(reservationViewModel.successMessage) }
    LaunchedEffect(reservationViewModel.successMessage) {
        val current = reservationViewModel.successMessage
        if (current != null && current != previousSuccess) {
            onDismiss()
        }
        previousSuccess = current
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                    val today = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                    return utcTimeMillis >= today && date.dayOfWeek != DayOfWeek.SUNDAY
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year >= LocalDate.now().year
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Aceptar", color = UcaBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UcaBlue, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(top = 10.dp, bottom = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.2.dp)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.size(48.dp))

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (editingReservation == null) "Reservación de\n${room.name}" else "Editar Reserva\n${room.name}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Capacidad de ${room.minCapacity}–${room.maxCapacity} personas",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Default.Close, "Cerrar", tint = Color.White)
                        }
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth().weight(1f), color = Color.White) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).navigationBarsPadding()) {

                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 16.dp)) {

                        Text("Número de personas que utilizarán la sala", fontWeight = FontWeight.Bold, color = UcaBlue, fontSize = 16.sp)
                        Text("Disponible para ${room.minCapacity}–${room.maxCapacity} personas", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedPeople,
                            onExpandedChange = { expandedPeople = !expandedPeople }
                        ) {
                            OutlinedTextField(
                                value = "$selectedPeople personas",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeople) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UcaBlue, unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(expanded = expandedPeople, onDismissRequest = { expandedPeople = false }, modifier = Modifier.background(Color.White)) {
                                peopleOptions.forEach { num ->
                                    DropdownMenuItem(text = { Text("$num personas") }, onClick = { selectedPeople = num; expandedPeople = false })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Seleccionar fecha de reserva", fontWeight = FontWeight.Bold, color = UcaBlue, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedDate.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("es", "ES"))),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = UcaBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = Color.Black,
                                    disabledBorderColor = Color.LightGray,
                                    disabledContainerColor = Color.White,
                                    disabledTrailingIconColor = UcaBlue
                                ),
                                enabled = false
                            )
                            Surface(
                                modifier = Modifier.matchParentSize().clickable { showDatePicker = true },
                                color = Color.Transparent
                            ) {}
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Hora de inicio", fontWeight = FontWeight.Bold, color = UcaBlue, fontSize = 16.sp)
                        Text("Despliega para seleccionar una hora disponible", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedTime,
                            onExpandedChange = { if (availableTimes.isNotEmpty()) expandedTime = !expandedTime }
                        ) {
                            OutlinedTextField(
                                value = if (availableTimes.isEmpty()) "Sin horarios disponibles" else if (selectedTime.isEmpty()) "Seleccionar hora" else selectedTime,
                                onValueChange = {},
                                readOnly = true,
                                enabled = availableTimes.isNotEmpty(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTime) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UcaBlue, unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(expanded = expandedTime, onDismissRequest = { expandedTime = false }, modifier = Modifier.background(Color.White)) {
                                availableTimes.forEach { time ->
                                    DropdownMenuItem(text = { Text(time) }, onClick = { selectedTime = time; expandedTime = false })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Duración de la reserva", fontWeight = FontWeight.Bold, color = UcaBlue, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedDuration,
                            onExpandedChange = { expandedDuration = !expandedDuration }
                        ) {
                            OutlinedTextField(
                                value = formatDuration(selectedDuration),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuration) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = UcaBlue, unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(expanded = expandedDuration, onDismissRequest = { expandedDuration = false }, modifier = Modifier.background(Color.White)) {
                                durationOptions.forEach { dur ->
                                    DropdownMenuItem(text = { Text(formatDuration(dur)) }, onClick = { selectedDuration = dur; expandedDuration = false })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (reservationViewModel.errorMessage != null) {
                            Text(
                                text = reservationViewModel.errorMessage!!,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (selectedTime.isNotEmpty()) {
                                    val dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    val minutes = (selectedDuration * 60).toLong()
                                    val endTime = LocalTime.parse(selectedTime, DateTimeFormatter.ofPattern("HH:mm"))
                                        .plusMinutes(minutes)
                                        .format(DateTimeFormatter.ofPattern("HH:mm"))

                                    if (editingReservation == null) {
                                        reservationViewModel.makeReservation(
                                            carnet = carnet, room = room, date = dateString,
                                            startTime = selectedTime, endTime = endTime, peopleCount = selectedPeople
                                        )
                                    } else {
                                        reservationViewModel.editReservation(
                                            reservationId = editingReservation.id,
                                            carnet = carnet, room = room, date = dateString,
                                            startTime = selectedTime, endTime = endTime, peopleCount = selectedPeople
                                        )
                                    }
                                }
                            },
                            enabled = selectedTime.isNotEmpty() && !reservationViewModel.isLoading,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UcaBlue, disabledContainerColor = Color.LightGray),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            if (reservationViewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    if (editingReservation == null) "Confirmar reserva" else "Actualizar reserva",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                "Cancelar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
