package com.dutisoft.ommmm

import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.dutisoft.ommmm.ui.theme.OmmmmTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmmmmTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* No hace nada aquí */ },
                            containerColor = Color(0xFF03DAC5), // teal_200
                            contentColor = Color(0xFFFFFFFF) // white
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->
                    DashboardScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf("") }
    val practices = remember(selectedDate) { generatePracticesForDate(selectedDate) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Número de racha máxima centrado
            Text(
                text = "7", // Reemplaza con el valor dinámico si es necesario
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF6200EE), // purple_500
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Racha máxima",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF000000), // black
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // CalendarView integrado en Compose
            AndroidView(
                factory = { context ->
                    CalendarView(context).apply {
                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            selectedDate = "$dayOfMonth/${month + 1}/$year"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de prácticas por día
            Text(
                text = "$selectedDate",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF000000), // black
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(practices) { practice ->
                    PracticeItem(practice)
                }
            }
        }

        // Texto de "Cerrar sesión" alineado al fondo de la pantalla
        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable {
                    handleLogout()
                }
                .padding(bottom = 16.dp) // Espaciado desde el borde inferior
        )
    }
}

fun handleLogout() {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    auth.signOut()

    // Aquí puedes navegar a la pantalla de inicio de sesión o mostrar un mensaje
    println("Sesión cerrada exitosamente") // Cambia esto por navegación si es necesario
}



@Composable
fun PracticeItem(practice: String) {
    val icons = listOf(
        painterResource(android.R.drawable.ic_menu_edit),
        painterResource(android.R.drawable.ic_menu_agenda),
        painterResource(android.R.drawable.ic_menu_info_details)
    )
    val randomIcon = remember { icons.random() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)), // white
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = randomIcon,
                contentDescription = "Ícono de práctica",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = practice,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3700B3) // purple_700
            )
        }
    }
}

fun generatePracticesForDate(date: String): List<String> {
    return if (date.isNotEmpty()) {
        listOf("Práctica 1 $date", "Práctica 2 $date", "Práctica 3 $date")
    } else {
        emptyList()
    }
}

@Composable
fun ImageSection() {
    val image: Painter = painterResource(id = R.drawable.fake_meditation_banner) // Reemplaza con tu imagen

    Image(
        painter = image,
        contentDescription = "Imagen rectangular",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                // No hace nada al hacer clic
            }
    )
}

@Composable
@Preview(showBackground = true)
fun DashboardPreview() {
    OmmmmTheme {
        DashboardScreen()
    }
}
