package com.dutisoft.ommmm

import android.media.MediaPlayer
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
    var lastClickTime by remember { mutableStateOf(0L) }
    var isTimeUp by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val doubleClickThreshold = 800L
    val context = LocalContext.current

    var countDownTimer by remember {
        mutableStateOf<CountDownTimer?>(null)
    }

    // Reproducir el sonido al iniciar la práctica
    fun playSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.tibetan_bowl)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()  // Libera los recursos cuando termine la reproducción
        }
    }

    // Función para iniciar el temporizador
    fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                timeRemaining = 0
                isTimerRunning = false
                isTimeUp = true
            }
        }.start()
        isTimerRunning = true
    }

    // Función para pausar el temporizador
    fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    // Maneja el clic para iniciar o pausar el temporizador
    fun handleClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickThreshold) {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
                playSound()  // Reproducir el sonido al iniciar
            }
        } else {
            Toast.makeText(context, "Toca dos veces para iniciar/pausar", Toast.LENGTH_SHORT).show()
        }
        lastClickTime = currentTime
    }

    // Formato de tiempo en minutos:segundos
    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // Interfaz de usuario
    Box(modifier = Modifier.fillMaxSize().clickable { handleClick() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = formatTime(timeRemaining),
                fontSize = 50.sp,
                modifier = Modifier.clickable {
                    if (!isTimerRunning) showTimePicker = true
                }
            )

            if (showTimePicker) {
                TimePickerDialog(
                    initialMinutes = (timeRemaining / 60000).toInt(),
                    onDismiss = { showTimePicker = false },
                    onConfirm = { selectedMinutes ->
                        timeRemaining = selectedMinutes * 60 * 1000L
                        showTimePicker = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableStateOf(initialMinutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona el tiempo") },
        text = {
            Column {
                Text("Elige minutos (máx 60):")
                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = it.toInt() },
                    valueRange = 1f..60f,
                    steps = 59
                )
                Text("${selectedMinutes} min", fontSize = 18.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedMinutes) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OmmmmTheme {
        MeditationScreen()
    }
}
