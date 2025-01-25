package com.dutisoft.ommmm

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.dutisoft.ommmm.ui.theme.OmmmmTheme

class MeditationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeditationScreen()
        }
    }
}

@Composable
fun MeditationScreen() {
    var timeRemaining by remember { mutableStateOf(60000L) }  // 1 minuto en milisegundos
    var isTimerRunning by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }  // Para medir el tiempo entre clics rápidos
    val doubleClickThreshold = 800L  // Umbral en milisegundos para considerar un doble clic rápido

    // Timer logic
    val countDownTimer = rememberUpdatedState(
        object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                timeRemaining = 0
                isTimerRunning = false
            }
        }
    )

    // Formato de tiempo
    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // Lógica para manejar el doble clic
    fun handleClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickThreshold) {
            // Doble clic detectado, pausar o reanudar
            if (isTimerRunning) {
                // Detener el temporizador
                countDownTimer.value.cancel()
                isTimerRunning = false
            } else {
                // Reanudar el temporizador
                countDownTimer.value.start()
                isTimerRunning = true
            }
        }
        lastClickTime = currentTime
    }

    // Interfaz de usuario
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().clickable {
                handleClick() // Llamamos al método que maneja el clic
                if (!isTimerRunning) {
                    // Iniciar el temporizador si no está corriendo
                    countDownTimer.value.start()
                    isTimerRunning = true
                }
            }
        ) {
            Text(
                text = formatTime(timeRemaining),
                fontSize = 50.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            if (timeRemaining == 0L) {
                Toast.makeText(
                    LocalContext.current,
                    "Tiempo agotado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OmmmmTheme {
        MeditationScreen()
    }
}
