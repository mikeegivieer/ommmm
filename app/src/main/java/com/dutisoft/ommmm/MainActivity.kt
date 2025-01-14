package com.dutisoft.ommmm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dutisoft.ommmm.ui.theme.OmmmmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmmmmTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoginForm()
                }
            }
        }
    }
}

@Composable
fun LoginForm() {
    // Estado para manejar los valores y errores
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Login", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        // Campo de email con validación
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (isValidEmail(it)) null else "Invalid email address"
            },
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(text = emailError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        // Campo de contraseña con validación
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (isValidPassword(it)) null else "Password must be at least 8 characters, include uppercase, lowercase, and a number"
            },
            label = { Text("Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        // Botón de login
        Button(
            onClick = {
                if (emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()) {
                    // Acción de login válida
                    println("Login successful!")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text(text = "Login")
        }
    }
}

// Función para validar correos electrónicos
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
    return emailRegex.matches(email)
}

// Función para validar contraseñas
fun isValidPassword(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    OmmmmTheme {
        LoginForm()
    }
}
