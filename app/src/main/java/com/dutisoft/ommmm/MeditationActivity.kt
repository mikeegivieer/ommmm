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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.*
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

    val countDownTimer = rememberUpdatedState(
        object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                timeRemaining = 0
                isTimerRunning = false
                isTimeUp = true
            }
        }
    )

    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun handleClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < doubleClickThreshold) {
            if (isTimerRunning) {
                countDownTimer.value.cancel()
                isTimerRunning = false
            } else {
                countDownTimer.value.start()
                isTimerRunning = true
            }
        }
        lastClickTime = currentTime
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.celebration))
    val progress by animateLottieCompositionAsState(composition)

    Box(modifier = Modifier.fillMaxSize()) {
        if (isTimeUp) {
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .zIndex(-1f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable { handleClick() }
        ) {
            Text(
                text = formatTime(timeRemaining),
                fontSize = 50.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { showTimePicker = true }  // Mostrar diálogo al hacer clic
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

            if (timeRemaining == 0L && !isTimeUp) {
                Toast.makeText(
                    LocalContext.current,
                    "Tiempo agotado",
                    Toast.LENGTH_SHORT
                ).show()
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
