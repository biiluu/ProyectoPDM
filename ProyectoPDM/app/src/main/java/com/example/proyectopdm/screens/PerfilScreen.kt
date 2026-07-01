package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopdm.viewmodel.ProfileViewModel


@Composable
fun PerfilScreen(
    carnet: String,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController
) {
    val darkBlue = Color(0xFF1D3354)
    val lightGray = Color(0xFFE9EDF0)

    LaunchedEffect(carnet) {
        viewModel.loadUser(carnet)
    }

    val user = viewModel.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Profile Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkBlue)
                .padding(top = 48.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initials = if (user != null && user.name.isNotEmpty()) {
                                    user.name.split(" ")
                                        .filter { it.isNotEmpty() }
                                        .take(2)
                                        .map { it[0] }
                                        .joinToString("")
                                        .uppercase()
                                } else ".."

                                Text(
                                    text = initials,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user?.name ?: "Cargando...",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Carnet ${user?.carnet ?: carnet}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Estudiante · ${user?.career ?: ""}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // Stats and Menu Items
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Stats Cards
            ProfileStatItem(
                icon = Icons.Default.DateRange,
                label = "Reservas totales",
                value = viewModel.reservationCount.toString()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileStatItem(
                icon = Icons.Default.AccessTime,
                label = "Horas reservadas",
                value = String.format("%.1f Hrs", viewModel.totalHours)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileStatItem(
                icon = Icons.Default.AccountBalance,
                label = "Edificio favorito",
                value = "Biblioteca"
            )

            Spacer(modifier = Modifier.height(24.dp))


            ProfileMenuItem(
                label = "Notificaciones",
                onClick = { navController.navigate("notifications/$carnet") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(
                label = "Privacidad",
                onClick = { navController.navigate("privacy") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(label = "Ayuda y soporte",
                onClick = { navController.navigate("help") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(label = "Acerca de")

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar Sesión"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileStatItem(icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProfileMenuItem(label: String, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick()},
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
