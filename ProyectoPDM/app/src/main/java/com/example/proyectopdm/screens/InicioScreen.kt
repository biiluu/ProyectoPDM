package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectopdm.data.entities.StudyRoom
import com.example.proyectopdm.ui.theme.*
import com.example.proyectopdm.viewmodel.ProfileViewModel
import com.example.proyectopdm.viewmodel.StudyRoomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun InicioScreen(
    carnet: String,
    onFloorClick: (Int) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    roomViewModel: StudyRoomViewModel = viewModel()
) {
    LaunchedEffect(carnet) {
        profileViewModel.loadUser(carnet)
    }

    val user = profileViewModel.user
    val availableRooms by roomViewModel.availableNowRooms.collectAsState()

    val currentDate = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (HUD)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBlue)
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Bienvenido de vuelta",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = user?.name ?: "Usuario",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentDate,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }

                // Notification Icon
                Box {
                    Surface(
                        modifier = Modifier.size(45.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                    )
                }
            }
        }

        // Reservation Card
        Box(modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes reservas próximas",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hacer una reserva >",
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onFloorClick(0) } // Va a explorar todas
                    )
                }
            }
        }

        // Section: Disponibles ahora
        SectionHeader(title = "Disponibles ahora", onSeeMoreClick = { onFloorClick(0) })

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableRooms.take(10)) { room ->
                RoomCard(room)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Nuestras Instalaciones
        Text(
            text = "Nuestras Instalaciones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen por niveles
        FloorSummaryCard(level = 1, description = "3 Salas de estudio", roomsCount = 3, onClick = { onFloorClick(1) })
        Spacer(modifier = Modifier.height(8.dp))
        FloorSummaryCard(level = 2, description = "3 Salas y 8 Cubículos individuales", roomsCount = 11, onClick = { onFloorClick(2) })
        Spacer(modifier = Modifier.height(8.dp))
        FloorSummaryCard(level = 3, description = "4 Salas, Taller Digital y Recreativa", roomsCount = 6, onClick = { onFloorClick(3) })

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FloorSummaryCard(level: Int, description: String, roomsCount: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = level.toString(), fontWeight = FontWeight.Bold, color = AccentBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Planta $level", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = description, color = Color.Gray, fontSize = 14.sp)
            }
            Text(text = "$roomsCount espacios", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RoomCard(room: StudyRoom) {
    Surface(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                Surface(
                    color = SuccessGreenBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Disponible",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (room.name.contains("Cubículo")) "Espacio Individual" else "Sala de Reunión",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = room.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(text = "Nivel ${room.floor}", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Outlined.People, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(text = "Cap. ${room.maxCapacity}", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeMoreClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Ver más",
            color = AccentBlue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSeeMoreClick() }
        )
    }
}
