package com.example.proyectopdm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectopdm.viewmodel.TermViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsBottomSheet(
    roomName: String,
    carnet: String,
    termViewModel: TermViewModel,
    onDismiss: () -> Unit,
    onTermsAccepted: () -> Unit
) {
    var accepted by remember { mutableStateOf<Boolean?>(null) }
    val UcaBlue = Color(0xFF1D3354)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = UcaBlue,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.5f))
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UcaBlue, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(top = 10.dp, bottom = 20.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(48.dp))

                    Text(
                        text = "Reservación de\n$roomName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 40.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "• CONDICIONES:",
                        fontWeight = FontWeight.Bold,
                        color = UcaBlue,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val condiciones = listOf(
                        "La sala de reunión es solamente para personal administrativo y académico de la universidad.",
                        "Presentarnos al escritorio de préstamo a la hora solicitada en el formulario de reserva de sala de estudio.",
                        "Dejar el carné en el escritorio de préstamos, de todos los usuarios de la sala.",
                        "No comer, ni ingerir bebidas.",
                        "No se permiten los juegos de mesa en la sala.",
                        "Utilizar las salas para fines académicos.",
                        "Evitar actos inmorales.",
                        "Notificar en el escritorio de préstamo, cuando finalice la reserva de la sala."
                    )

                    condiciones.forEach { condicion ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", fontWeight = FontWeight.Bold, color = UcaBlue, fontSize = 14.sp)
                            Text(text = condicion, fontSize = 14.sp, color = Color(0xFF333333), lineHeight = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "El Centro de Recursos para el Aprendizaje y la Investigación P. Florentino Idoate, S.J. se reserva el derecho de admisión en caso de no cumplir con las condiciones para el uso de la sala, la reserva de la misma será cancelada por el CRAI y se procederá a realizar la sanción según el Reglamento de Servicios del CRAI.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Habiendo leído las condiciones anteriores:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { accepted = true }.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = accepted == true,
                            onClick = { accepted = true },
                            colors = RadioButtonDefaults.colors(selectedColor = UcaBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Comprendo y acepto las condiciones.", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { accepted = false }.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = accepted == false,
                            onClick = { accepted = false },
                            colors = RadioButtonDefaults.colors(selectedColor = UcaBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No estoy de acuerdo", fontSize = 14.sp, color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (accepted == true) {
                                termViewModel.acceptTerms(carnet, onComplete = onTermsAccepted)
                            } else if (accepted == false) {
                                onDismiss() // Si no acepta, se cierra
                            }
                        },
                        enabled = accepted != null && !termViewModel.isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UcaBlue),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (termViewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Confirmar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}