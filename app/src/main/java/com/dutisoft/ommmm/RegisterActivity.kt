package com.dutisoft.ommmm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dutisoft.ommmm.ui.theme.OmmmmTheme
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            OmmmmTheme {
                RegisterForm { email, password, name ->
                    registerWithFirebase(email, password, name)
                }
            }
        }
    }

    private fun registerWithFirebase(email: String, password: String, name: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful! Welcome, $name", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar volver atrás
    }
}

@Composable
fun RegisterForm(onRegister: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Register", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        // Campo de nombre
        TextField(
            value = name,
            onValueChange = {
                name = it
                nameError = if (it.isNotEmpty()) null else "Name cannot be empty"
            },
            label = { Text("Full Name") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError != null) {
            Text(text = nameError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        // Campo de email
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

        // Campo de contraseña
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (isValidPassword(it)) null else "Password must meet requirements"
            },
            label = { Text("Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        // Campo de confirmación de contraseña
        TextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = if (it == password) null else "Passwords do not match"
            },
            label = { Text("Confirm Password") },
            isError = confirmPasswordError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        // Botón de registro
        Button(
            onClick = {
                if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
                    onRegister(email, password, name)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            Text(text = "Register")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    OmmmmTheme {
        RegisterForm { _, _, _ -> }
    }
}
