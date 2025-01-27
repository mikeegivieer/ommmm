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
                        FloatingActionButton(onClick = { /* No hace nada aquí */ }) {
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

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Número de racha máxima centrado
        Text(
            text = "7", // Reemplaza con el valor dinámico si es necesario
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Racha máxima",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
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
            text = "Prácticas para $selectedDate",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(practices) { practice ->
                PracticeItem(practice)
            }
        }
    }
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
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun generatePracticesForDate(date: String): List<String> {
    return if (date.isNotEmpty()) {
        listOf("Práctica 1 para $date", "Práctica 2 para $date", "Práctica 3 para $date")
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
            .fillMaxWidth() // Esto hace que la imagen ocupe el ancho completo
            .height(200.dp) // Esto define una altura fija para la imagen rectangular
            .padding(8.dp) // Añade un pequeño margen
            .clip(RoundedCornerShape(8.dp)) // Esto hace que tenga bordes redondeados (opcional)
            .clickable {
                // No hace nada al hacer clic
            }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    OmmmmTheme {
        DashboardScreen()
    }
}
