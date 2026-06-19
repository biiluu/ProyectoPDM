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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectopdm.R
import com.example.proyectopdm.viewmodel.LoginViewModel

val TextFieldBg = Color(0xFFE8E4EB)
val UcaBlueLogin = Color(0xFF1f194f)

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
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
                value = viewModel.carne,
                onValueChange = { 
                    viewModel.carne = it
                    viewModel.errorMessage = null
                },
                label = { Text("Carnet") },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = TextFieldBg),
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = viewModel.password,
                onValueChange = { 
                    viewModel.password = it
                    viewModel.errorMessage = null
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = TextFieldBg),
                enabled = !viewModel.isLoading
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = { viewModel.onLogin(onLoginSuccess) },
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
}
