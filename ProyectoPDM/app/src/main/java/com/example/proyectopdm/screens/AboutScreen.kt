package com.example.proyectopdm.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    // Definimos el color azul exacto solicitado: 1D3354
    val topBarBlue = Color(0xFF1D3354)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Acerca de",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "La historia del CRAI P. Florentino Idoate, S.J. incluye importantes hitos de ampliación, modernización tecnológica y resiliencia que consolidaron su infraestructura actual: [1, 2]",
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            // Secciones para cada hito histórico
            HitoHistorico(
                anio = "1980",
                titulo = "Ampliación de colecciones especiales y resguardo",
                descripcion = "Durante el inicio del conflicto armado en El Salvador, la biblioteca se convirtió en un refugio documental clave, expandiendo su archivo histórico para proteger valiosos documentos nacionales y eclesiásticos de la época."
            )

            HitoHistorico(
                anio = "1999–2000",
                titulo = "Automatización e internet",
                descripcion = "Se implementaron los primeros catálogos electrónicos accesibles en línea, eliminando progresivamente los ficheros físicos de tarjetas y transformando radicalmente la forma de buscar información."
            )

            HitoHistorico(
                anio = "2019",
                titulo = "Inicio del gran proyecto de infraestructura y ampliación física",
                descripcion = "El 1 de marzo comenzó la primera gran fase de remodelación del edificio. Se intervino la primera planta para ampliar los espacios comunes, crear áreas de lectura abiertas, cubículos modernos y un área de lectura infantil. [1, 2, 3]"
            )

            HitoHistorico(
                anio = "2024",
                titulo = "Culminación y reinauguración oficial",
                descripcion = "El 19 de marzo de 2024, la universidad reinauguró oficialmente el edificio tras completar todas las etapas del equipamiento y la readecuación tecnológica iniciadas en 2019. Con este hito se consolidó la transición definitiva de una biblioteca tradicional a un centro abierto, dinámico e interactivo."
            )
        }
    }
}

@Composable
fun HitoHistorico(anio: String, titulo: String, descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$anio: $titulo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1D3354) // También aplicamos el mismo tono azul a los títulos de los hitos
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descripcion,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}