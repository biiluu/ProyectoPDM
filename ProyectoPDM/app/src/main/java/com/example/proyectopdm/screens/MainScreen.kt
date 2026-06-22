package com.example.proyectopdm.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.proyectopdm.ui.theme.DarkBlue
import androidx.navigation.NavController


@Composable
fun MainScreen(carnet: String, onLogout: () -> Unit,navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var filterFloor by remember { mutableIntStateOf(0) } // 0 means all floors
    
    val items = listOf("Inicio", "Explorar", "Reservas", "Perfil")
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.Search,
        Icons.Outlined.DateRange,
        Icons.Outlined.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF1f194f)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { 
                            selectedItem = index
                            if (index != 1) filterFloor = 0
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBlue,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFF1f194f),
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> InicioScreen(
                    carnet = carnet,
                    onFloorClick = { floor: Int ->
                        filterFloor = floor
                        selectedItem = 1
                    }
                )
                1 -> ExplorarScreen(initialFloor = filterFloor)
                2 -> ReservasScreen(navController = navController, carnet = carnet)
                3 -> PerfilScreen(carnet = carnet, onLogout = onLogout, navController = navController)
            }
        }
    }
}
