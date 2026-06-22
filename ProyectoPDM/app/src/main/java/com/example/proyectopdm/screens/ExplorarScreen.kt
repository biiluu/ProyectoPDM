package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.ui.theme.*
import com.example.proyectopdm.viewmodel.StudyRoomViewModel

@Composable
fun ExplorarScreen(
    initialFloor: Int = 0,
    viewModel: StudyRoomViewModel = viewModel()
) {
    var selectedFloor by remember { mutableIntStateOf(initialFloor) }
    val rooms by viewModel.rooms.collectAsState()

    val filteredRooms = if (selectedFloor == 0) {
        rooms
    } else {
        rooms.filter { it.floor == selectedFloor }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkBlue,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Explorar Salas",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloorChip(label = "Todas", selected = selectedFloor == 0) { selectedFloor = 0 }
                    FloorChip(label = "Nivel 1", selected = selectedFloor == 1) { selectedFloor = 1 }
                    FloorChip(label = "Nivel 2", selected = selectedFloor == 2) { selectedFloor = 2 }
                    FloorChip(label = "Nivel 3", selected = selectedFloor == 3) { selectedFloor = 3 }
                }
            }
        }

        // Rooms List
        if (filteredRooms.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay salas disponibles en este nivel", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRooms) { room ->
                    ExploreRoomCard(room)
                }
            }
        }
    }
}

@Composable
fun FloorChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) AccentBlue else Color.White.copy(alpha = 0.1f),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ExploreRoomCard(room: StudyRoom) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Nivel ${room.floor}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Capacidad: ${room.minCapacity}-${room.maxCapacity}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Button(
                onClick = { /* Ir a reservar */ },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Reservar", fontSize = 12.sp)
            }
        }
    }
}