package com.example.classmate.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Paleta
private val FondoGradient = Brush.verticalGradient(
    listOf(Color(0xFF191220), Color(0xFF241F2B))
)
private val TarjetaTarea = Color(0xFF7E6AF4)      // Lila
private val FondoBoton = Color(0xFF6DBA4E)        // Verde
private val TextoPrincipal = Color(0xFFF5F3F6)    // Blanco lavanda

@Composable
fun TasksScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate("tareas") },
                colors = ButtonDefaults.buttonColors(containerColor = TarjetaTarea),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Tareas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextoPrincipal
                )
            }
            Button(
                onClick = { navController.navigate("examenes") },
                colors = ButtonDefaults.buttonColors(containerColor = FondoBoton),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
            ) {
                Text(
                    text = "Exámenes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            EmojiButtonsRow(
                onSubjectsClick = { navController.navigate("current_class") },
                onCalendarClick = { navController.navigate("schedule") },
                onTasksClick = { navController.navigate("tasks") }
            )
        }
    }
}
