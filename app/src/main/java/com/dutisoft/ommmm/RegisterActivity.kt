package com.dutisoft.ommmm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class RegisterActivity : AppCompatActivity()  {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        enableEdgeToEdge() // Mover esto fuera de setContent
        setContent {
            OmmmmTheme {
                RegisterForm { email, password, name, dob ->
                    registerWithFirebase(email, password, name, dob)
                }
            }
        }
    }

    private fun registerWithFirebase(email: String, password: String, name: String, dob: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful! Welcome, $name", Toast.LENGTH_SHORT).show()
                    loginAfterRegistration(email, password)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginAfterRegistration(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToDashboardActivity()  // Cambié a DashboardActivity
                } else {
                    Toast.makeText(this, "Auto-login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToDashboardActivity() {  // Cambié el nombre a DashboardActivity
        val intent = Intent(this, DashboardActivity::class.java)  // Apunta a DashboardActivity
        startActivity(intent)
        finish()
    }
}

@Composable
fun RegisterForm(onRegister: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Register", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

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

        TextField(
            value = dob,
            onValueChange = {
                dob = it
                dobError = if (isValidDob(it)) null else "Enter a valid date (YYYY-MM-DD) and must be at least 18 years old"
            },
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            isError = dobError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (dobError != null) {
            Text(text = dobError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

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

        Button(
            onClick = {
                if (nameError == null && emailError == null && dobError == null && passwordError == null && confirmPasswordError == null) {
                    onRegister(email, password, name, dob)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && email.isNotEmpty() && dob.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            Text(text = "Register")
        }
    }
}

fun isValidDob(dob: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(dob, formatter)
        val currentDate = LocalDate.now()
        val age = ChronoUnit.YEARS.between(birthDate, currentDate)
        age >= 18
    } catch (e: DateTimeParseException) {
        false
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    OmmmmTheme {
        RegisterForm { _, _, _, _ -> }
    }
}
