package com.example.classmate.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.classmate.data.models.Horario
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

private val FondoPrincipal = Color(0xFF191220)
private val FondoTarjeta = Color(0xFF7E6AF4)
private val BordeTarjeta = Color(0xFFC09BE3)
private val TextoPrincipal = Color(0xFFF5F3F6)
private val VerdeExamen = Color(0xFF6DBA4E)
private val GrisLavanda = Color(0xFFD3D3E6)
private val FondoBarraInferior = Color(0xFF241F2B)
private val FondoBotonEmoji = Color(0xFFF5F3F6)
private val EmojiColor = Color(0xFF191220)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "current_class"
    ) {
        composable("current_class") { CurrentClassScreen(navController) }
        composable("tasks") { TasksScreen(navController) }
        composable("schedule") { TodayScheduleScreen(navController) }
        composable("tareas") { TareasScreen(navController) }
        composable("examenes") { ExamsScreen(navController) }
    }
}

@Composable
fun CurrentClassScreen(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }
    var currentClass by remember { mutableStateOf<Horario?>(null) }

    // Obtén el día de hoy en español (capitalizado)
    val dayOfWeek = remember {
        val locale = Locale("es", "MX")
        val sdf = SimpleDateFormat("EEEE", locale)
        sdf.format(Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    LaunchedEffect(dayOfWeek) {
        isLoading = true
        val db = FirebaseFirestore.getInstance()
        db.collection("horarios").document(dayOfWeek)
            .get()
            .addOnSuccessListener { doc ->
                val clases = doc.get("clases") as? List<Map<String, Any>>
                val now = Calendar.getInstance()
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMinute = now.get(Calendar.MINUTE)

                // Busca si hay clase en este momento
                val claseActual = clases?.mapNotNull { map ->
                    try {
                        Horario(
                            id = "",
                            materia = map["materia"]?.toString() ?: "",
                            hora_inicio = map["hora_inicio"]?.toString() ?: "",
                            hora_fin = map["hora_fin"]?.toString() ?: "",
                            profesor = map["profesor"]?.toString() ?: "",
                            dia = dayOfWeek
                        )
                    } catch (e: Exception) {
                        null
                    }
                }?.firstOrNull { horario ->
                    // Compara la hora actual con la hora_inicio y hora_fin
                    try {
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val horaInicio = sdf.parse(horario.hora_inicio)
                        val horaFin = sdf.parse(horario.hora_fin)
                        val ahora = sdf.parse(String.format("%02d:%02d", currentHour, currentMinute))
                        horaInicio != null && horaFin != null && ahora != null &&
                                !ahora.before(horaInicio) && ahora.before(horaFin)
                    } catch (e: Exception) {
                        false
                    }
                }

                currentClass = claseActual
                isLoading = false
            }
            .addOnFailureListener {
                currentClass = null
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        when {
            isLoading -> {
                // Spinner centrado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TextoPrincipal)
                }
            }
            currentClass != null -> {
                // Hay clase actual
                CurrentClassCard(
                    subject = currentClass!!.materia,
                    time = "${currentClass!!.hora_inicio} - ${currentClass!!.hora_fin}",
                    location = currentClass!!.profesor
                )
            }
            else -> {
                // No hay clase en este momento (centrado)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay clase en este\nmomento",
                        fontSize = 18.sp,
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        EmojiButtonsRow(
            onSubjectsClick = { navController.navigate("current_class") },
            onCalendarClick = { navController.navigate("schedule") },
            onTasksClick = { navController.navigate("tasks") }
        )
    }
}

@Composable
fun CurrentClassCard(subject: String, time: String, location: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BordeTarjeta, RoundedCornerShape(16.dp))
                .background(FondoTarjeta)
                .padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = subject,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoPrincipal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTUAL",
                        fontSize = 9.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(VerdeExamen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = time, fontSize = 11.sp, color = TextoPrincipal)
                Text(text = location, fontSize = 10.sp, color = GrisLavanda)
            }
        }
    }
}

@Composable
fun EmojiButton(emoji: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(FondoBotonEmoji)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            color = EmojiColor
        )
    }
}

@Composable
fun EmojiButtonsRow(
    onSubjectsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onTasksClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = FondoBarraInferior,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiButton("📚", onClick = onSubjectsClick)
            EmojiButton("📅", onClick = onCalendarClick)
            EmojiButton("✅", onClick = onTasksClick)
        }
    }
}

// Nota: Asegúrate que en TasksScreen y TodayScheduleScreen
// el tipo de parámetro sea NavHostController y navega SIEMPRE usando
// navController.navigate("nombre_de_ruta") CON LAS MISMAS RUTAS QUE AQUÍ.
