package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.example.proyectopdm.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    navController: NavController,
    carnet: String,
    viewModel: ReservationViewModel = viewModel()
) {
    val listaReservas by viewModel.getUserReservationsFlow(carnet).collectAsState(initial = emptyList())
    
    // FILTRO: Solo mostrar reservas que no han sido canceladas
    val reservasActivas = listaReservas.filter { 
        it.status != "CANCELADA_USUARIO" && it.status != "CANCELADA_INASISTENCIA" 
    }

    var reservaAEliminar by remember { mutableStateOf<Reservation?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1D3354))
            )
        }
    ) { paddingValues ->
        if (reservasActivas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No tienes reservas activas.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(reservasActivas) { reserva ->
                    ReservaCard(reserva = reserva, onDeleteClick = {
                        reservaAEliminar = reserva
                        mostrarDialogo = true
                    })
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
    }
}

@Composable
fun ReservaCard(reserva: Reservation, onDeleteClick: () -> Unit) {
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
                    Text(text = "Sala ID: ${reserva.roomId}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF154360))
                    Text(text = "📅 Fecha: ${reserva.date}")
                    Text(text = "⏰ Hora: ${reserva.startTime} - ${reserva.endTime}")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}
