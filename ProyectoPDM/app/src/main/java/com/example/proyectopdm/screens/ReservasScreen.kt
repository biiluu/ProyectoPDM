package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopdm.data.entities.Reservation
import com.example.proyectopdm.data.entities.ReservationWithRoom
import com.example.proyectopdm.viewmodel.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    navController: NavController,
    carnet: String,
    viewModel: ReservationViewModel = viewModel()
) {
    val listaReservasWithRoom by viewModel.getUserReservationsWithRoomFlow(carnet).collectAsState(initial = emptyList())
    
    // Ejecutar limpieza de inasistencias al entrar
    LaunchedEffect(Unit) {
        viewModel.checkAndCancelOverdueReservations(carnet)
    }

    // FILTRO: Solo mostrar reservas que no han sido canceladas
    val reservasActivas = listaReservasWithRoom.filter { 
        it.reservation.status != "CANCELADA_USUARIO" && it.reservation.status != "CANCELADA_INASISTENCIA" 
    }

    var reservaAEliminar by remember { mutableStateOf<Reservation?>(null) }
    var reservaAEditar by remember { mutableStateOf<ReservationWithRoom?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes de éxito o error del ViewModel
    LaunchedEffect(viewModel.successMessage, viewModel.errorMessage) {
        viewModel.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
        viewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1D3354))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (reservasActivas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes reservas activas.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(reservasActivas) { item ->
                        ReservaCard(
                            reservationWithRoom = item, 
                            onDeleteClick = {
                                reservaAEliminar = item.reservation
                                mostrarDialogo = true
                            },
                            onEditClick = {
                                reservaAEditar = item
                            },
                            onConfirmAttendance = {
                                viewModel.confirmAttendance(item.reservation)
                            }
                        )
                    }
                }
            }
        }

        if (mostrarDialogo && reservaAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Cancelar Reserva") },
                text = { Text("¿Estás seguro de que deseas cancelar esta reserva?") },
                confirmButton = {
                    TextButton(onClick = {
                        reservaAEliminar?.let { viewModel.cancelReservation(it) }
                        mostrarDialogo = false
                        reservaAEliminar = null
                    }) { Text("Sí, cancelar", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) { Text("Mantener") }
                }
            )
        }

        if (reservaAEditar != null) {
            ReservationBottomSheet(
                room = reservaAEditar!!.studyRoom,
                carnet = carnet,
                reservationViewModel = viewModel,
                onDismiss = { reservaAEditar = null },
                editingReservation = reservaAEditar!!.reservation
            )
        }
    }
}

@Composable
fun ReservaCard(
    reservationWithRoom: ReservationWithRoom, 
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onConfirmAttendance: () -> Unit
) {
    val reserva = reservationWithRoom.reservation
    val room = reservationWithRoom.studyRoom
    
    val now = LocalTime.now()
    val today = LocalDate.now().toString()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    
    val resStartTime = try { LocalTime.parse(reserva.startTime, formatter) } catch(e: Exception) { null }
    val esHoraDeCheckIn = resStartTime != null && reserva.date == today && !now.isBefore(resStartTime)
    val mostrarBotonConfirmar = esHoraDeCheckIn && reserva.status == "PENDIENTE"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFF1D3354)))
            
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = if (room.name.contains("Nivel", ignoreCase = true)) {
                        room.name
                    } else {
                        "${room.name} - Nivel ${room.floor}"
                    }

                    Text(
                        text = displayName,
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp, 
                        color = Color(0xFF154360)
                    )
                    Text(text = "📅 Fecha: ${reserva.date}")
                    Text(text = "⏰ Hora: ${reserva.startTime} - ${reserva.endTime}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val statusColor = when(reserva.status) {
                            "CONFIRMADA" -> Color(0xFF2E7D32)
                            else -> Color(0xFFF57C00)
                        }
                        val statusIcon = if (reserva.status == "CONFIRMADA") Icons.Default.CheckCircle else Icons.Default.Timer
                        
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if(reserva.status == "CONFIRMADA") "Asistencia Confirmada" else "Pendiente de Asistencia",
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Row {
                    if (reserva.status == "PENDIENTE") {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1D3354).copy(alpha = 0.7f))
                        }
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            }

            if (mostrarBotonConfirmar) {
                Button(
                    onClick = onConfirmAttendance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D3354)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar Asistencia")
                }
            }
        }
    }
}
