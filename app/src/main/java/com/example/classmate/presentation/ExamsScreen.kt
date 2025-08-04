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
import com.example.classmate.data.models.Examen
import com.google.firebase.firestore.FirebaseFirestore
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.items
import androidx.compose.material3.Text

private val FondoGradient = Brush.verticalGradient(
    listOf(Color(0xFF191220), Color(0xFF241F2B))
)
private val TextoPrincipal = Color(0xFFF5F3F6)
private val CardColor = Color(0xFF7E6AF4)
private val DueColor = Color(0xFFD3D3E6)

@Composable
fun ExamsScreen(navController: NavController) {
    var examenes by remember { mutableStateOf<List<Examen>>(emptyList()) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("examenes")
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { doc ->
                    try {
                        doc.toObject(Examen::class.java)
                    } catch (e: Exception) {
                        Log.e("FIRESTORE", "Error parseando Examen", e)
                        null
                    }
                }
                examenes = list
            }
            .addOnFailureListener { exception ->
                Log.w("FIRESTORE", "Error obteniendo examenes", exception)
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
            items(examenes) { examen ->
                ExamenItem(examen = examen)
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
fun ExamenItem(examen: Examen) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardColor)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = examen.materia,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextoPrincipal
            )
            if (examen.temas.isNotEmpty()) {
                Text(
                    text = "Temas: ${examen.temas}",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            if (examen.dueDate.isNotEmpty()) {
                Text(
                    text = "Fecha: ${examen.dueDate}",
                    fontSize = 11.sp,
                    color = DueColor
                )
            }
            if (examen.notification) {
                Text(
                    text = "¡Recordatorio activado!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF6DBA4E)
                )
            }
        }
    }
}
