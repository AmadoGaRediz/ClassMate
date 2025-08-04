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
import com.example.classmate.data.models.Task
import com.google.firebase.firestore.FirebaseFirestore
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.items
import androidx.compose.material3.Text
import kotlinx.coroutines.tasks.await

private val FondoGradient = Brush.verticalGradient(
    listOf(Color(0xFF191220), Color(0xFF241F2B))
)
private val TextoPrincipal = Color(0xFFF5F3F6)
private val CardColor = Color(0xFF7E6AF4)
private val CompletedColor = Color(0xFF6DBA4E)
private val DueColor = Color(0xFFD3D3E6)

@Composable
fun TareasScreen(navController: NavController) {
    var tareas by remember { mutableStateOf<List<Task>>(emptyList()) }

    // Cargar tareas desde Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { doc ->
                    try {
                        doc.toObject(Task::class.java)
                    } catch (e: Exception) {
                        Log.e("FIRESTORE", "Error parseando Task", e)
                        null
                    }
                }
                tareas = list
            }
            .addOnFailureListener { exception ->
                Log.w("FIRESTORE", "Error obteniendo tareas", exception)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGradient)
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(tareas) { tarea ->
                TaskItem(task = tarea)
                Spacer(modifier = Modifier.height(8.dp))
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
fun TaskItem(task: Task) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardColor)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextoPrincipal
            )
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            if (task.dueDate.isNotEmpty()) {
                Text(
                    text = "Fecha límite: ${task.dueDate}",
                    fontSize = 11.sp,
                    color = DueColor
                )
            }
            if (task.completed) {
                Text(
                    text = "¡Completada!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = CompletedColor
                )
            }
        }
    }
}
