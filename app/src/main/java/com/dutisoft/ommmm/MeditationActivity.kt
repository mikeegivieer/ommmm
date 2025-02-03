package com.dutisoft.ommmm

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutisoft.ommmm.ui.theme.OmmmmTheme

class MeditationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmmmmTheme {
                MeditationScreen()
            }
        }
    }
}

@Composable
fun MeditationScreen() {
    var timeRemaining by remember { mutableStateOf(60000L) }  // 1 minuto en milisegundos
    var isTimerRunning by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var countDownTimer by remember { mutableStateOf<CountDownTimer?>(null) }

    fun playSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.tibetan_bowl)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
    }

    fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                timeRemaining = 0
                isTimerRunning = false
            }
        }.start()
        isTimerRunning = true
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    fun handleClick() {
        if (isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
            playSound()
        }
    }

    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    Box(modifier = Modifier.fillMaxSize().clickable { handleClick() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = formatTime(timeRemaining),
                fontSize = 50.sp,
                modifier = Modifier.clickable { if (!isTimerRunning) showTimePicker = true }
            )

            Icon(
                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = stringResource(if (isTimerRunning) R.string.pause else R.string.play),
                modifier = Modifier.size(48.dp).padding(8.dp)
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
        title = { Text(stringResource(R.string.select_time)) },
        text = {
            Column {
                Text(stringResource(R.string.select_time))
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
                Text(stringResource(R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
