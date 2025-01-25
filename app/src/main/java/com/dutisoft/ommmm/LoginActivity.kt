package com.dutisoft.ommmm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dutisoft.ommmm.ui.theme.OmmmmTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity()  {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Verificar si hay un usuario autenticado
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Si el usuario está logeado, redirigir a DashboardActivity
            navigateToDashboard()
            return
        }

        enableEdgeToEdge()
        setContent {
            OmmmmTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoginForm(
                        onLogin = { email, password -> loginWithFirebase(email, password) },
                        onNavigateToRegister = { navigateToRegister() }
                    )
                }
            }
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Redirige a DashboardActivity después de login exitoso
                    navigateToDashboard()
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java) // Cambié a DashboardActivity
        startActivity(intent)
        finish()  // Finaliza MainActivity para evitar que el usuario regrese
    }
}

@Composable
fun LoginForm(onLogin: (String, String) -> Unit, onNavigateToRegister: () -> Unit) {
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
            Text(text = emailError!!, color = Color.Red)
        }

        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (isValidPassword(it)) null else "Password must be at least 8 characters"
            },
            label = { Text("Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = Color.Red)
        }

        Button(
            onClick = {
                if (emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()) {
                    onLogin(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text(text = "Login")
        }

        // Texto para registrar una nueva cuenta
        Text(
            text = "Si no tienes una cuenta, regístrate",
            color = Color.Blue,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onNavigateToRegister() }
        )
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 8
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    OmmmmTheme {
        LoginForm(onLogin = { _, _ -> }, onNavigateToRegister = {})
    }
}
