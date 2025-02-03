package com.dutisoft.ommmm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dutisoft.ommmm.ui.theme.OmmmmTheme
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val celebrationType = intent.getIntExtra("celebration_type", 0)

        if (celebrationType == 1) {
            triggerCelebration()
        }

        setContent {
            OmmmmTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(
                            onClick = {
                                val intent = Intent(context, MeditationActivity::class.java)
                                context.startActivity(intent)
                            },
                            containerColor = Color(0xFF03DAC5),
                            contentColor = Color(0xFFFFFFFF)
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        DashboardScreen(modifier = Modifier.padding(innerPadding))

                        if (celebrationType == 1) {
                            // Animación de confeti
                            KonfettiView(
                                modifier = Modifier.fillMaxSize(),
                                parties = listOf(
                                    Party(
                                        speed = 5f,
                                        maxSpeed = 10f,
                                        damping = 0.9f,
                                        angle = 270,
                                        spread = 360,
                                        timeToLive = 2000L,
                                        shapes = listOf(Shape.Square, Shape.Circle),
                                        size = listOf(Size.SMALL, Size.LARGE),
                                        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100)
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun triggerCelebration() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf("") }
    val practices = remember(selectedDate) { generatePracticesForDate(selectedDate) }
    val context = LocalContext.current

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
            Text(
                text = "7",
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF6200EE),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Racha máxima",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF000000),
                modifier = Modifier.padding(bottom = 16.dp)
            )

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

            Text(
                text = "$selectedDate",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF000000),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(practices) { practice ->
                    PracticeItem(practice)
                }
            }
        }

        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable {
                    handleLogout(context)
                }
                .padding(bottom = 16.dp)
        )
    }
}

fun handleLogout(context: Context) {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    auth.signOut()

    if (context is Activity) {
        context.finish()
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
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
                color = Color(0xFF3700B3)
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