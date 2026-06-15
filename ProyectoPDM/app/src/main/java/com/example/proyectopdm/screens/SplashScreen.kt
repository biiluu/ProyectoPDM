package com.example.proyectopdm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectopdm.R

val UcaBlue = Color(0xFF211A50)
@Composable
fun SplashScreen(onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UcaBlue),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CRAI", color = Color.White, fontSize = 40.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_uca),
            contentDescription = "Logo UCA",
            modifier = Modifier.size(180.dp)

        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onContinueClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = UcaBlue
            )
        ) {
            Text("Continuar")
        }
    }
}