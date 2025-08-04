package com.example.classmate.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.example.classmate.data.models.Horario
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.tasks.await


private val CardColors = listOf(
    Color(0xFF7E6AF4), // Lila
    Color(0xFF6DBA4E), // Verde
    Color(0xFFF8B14D), // Amarillo
    Color(0xFFFF5E5B)  // Rojo
)
private val CardTextColors = listOf(
    Color(0xFFF5F3F6),
    Color(0xFFF5F3F6),
    Color(0xFF333333),
    Color.White
)
private val CardHourColors = listOf(
    Color(0xFFC09BE3),
    Color(0xFF388E3C),
    Color(0xFFD4840E),
    Color(0xFFFFC1C1)
)
private val FondoGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF191220), Color(0xFF241F2B))
)

@Composable
fun TodayScheduleScreen(navController: NavController) {
    var schedule by remember { mutableStateOf<List<Horario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Día actual (por ejemplo: "Sábado", "Lunes", ...)
    val diaDeHoy = remember {
        SimpleDateFormat("EEEE", Locale("es", "MX")).format(Date())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "MX")) else it.toString() }
    }

    // Leer Firestore según la estructura real
    LaunchedEffect(diaDeHoy) {
        isLoading = true
        val db = FirebaseFirestore.getInstance()
        db.collection("horarios")
            .document(diaDeHoy)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val clasesList = document["clases"] as? List<Map<String, Any>>
                    val horarios = clasesList?.map { claseMap ->
                        Horario(
                            materia = claseMap["materia"]?.toString() ?: "",
                            hora_inicio = claseMap["hora_inicio"]?.toString() ?: "",
                            hora_fin = claseMap["hora_fin"]?.toString() ?: "",
                            profesor = claseMap["profesor"]?.toString() ?: "",
                            id = "",
                            dia = ""
                        )
                    } ?: emptyList()
                    schedule = horarios
                } else {
                    schedule = emptyList()
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.w("FIRESTORE", "Error obteniendo horario", exception)
                schedule = emptyList()
                isLoading = false
            }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = FondoGradient),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Título
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Horario de Hoy",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFFF5F3F6)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Lista de materias
        if (isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando...",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        } else if (schedule.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay clases para hoy",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        } else {
            ScalingLazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(schedule) { horario ->
                    ScheduleItem(
                        time = "${horario.hora_inicio} - ${horario.hora_fin}",
                        subject = horario.materia,
                        location = horario.profesor,
                        colorIndex = schedule.indexOf(horario) % CardColors.size
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Barra inferior de botones (igual que antes)
        EmojiButtonsRow(
            onSubjectsClick = { navController.navigate("current_class") },
            onCalendarClick = { /* Navegación */ },
            onTasksClick = { navController.navigate("tasks") }
        )
    }
}

@Composable
fun ScheduleItem(
    time: String,
    subject: String,
    location: String,
    colorIndex: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColors[colorIndex])
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = time,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CardHourColors[colorIndex]
            )
            Text(
                text = if (location.isNotEmpty()) "$subject - $location" else subject,
                fontSize = 13.sp,
                color = CardTextColors[colorIndex]
            )
        }
    }
}
