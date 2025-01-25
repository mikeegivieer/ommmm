package com.dutisoft.ommmm

import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    Column(modifier = modifier.padding(16.dp)) {
        // Sección de imagen
        ImageSection()

        // Espacio entre la imagen y el calendario
        Spacer(modifier = Modifier.height(16.dp))

        // CalendarView integrado en Compose
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        // Manejo del cambio de fecha si es necesario
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
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
