package com.example.proyectopdm.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectopdm.R

val TextFieldBg = Color(0xFFE8E4EB)
val UcaBlueLogin = Color(0xFF1f194f)


@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var carne by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(UcaBlueLogin)) {
        Image(
            painter = painterResource(id = R.drawable.logo_uca),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).padding(20.dp).size(50.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            TextField(
                value = carne,
                onValueChange = { carne = it },
                label = { Text("Carnet") },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = TextFieldBg)
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = TextFieldBg)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier.width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = UcaBlueLogin
                )
            ) {
                Text("Iniciar Sesión")
            }
        }
    }
}