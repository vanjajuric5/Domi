package com.example.domi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DomiTheme {
                RegisterScreen(
                    onRegistrationSuccess = { finish() },
                ) { finish() }
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegistrationSuccess: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(value = "") }
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var phone by remember { mutableStateOf(value = "") }
    var city by remember { mutableStateOf(value = "") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbHelper = remember { DatabaseHelper(context.applicationContext) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(26.dp)
                .verticalScroll(rememberScrollState())
                .testTag("register_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Registracija", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Ime i prezime") },
                modifier = Modifier.fillMaxWidth().testTag("name_input"),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth().testTag("email_input"),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Lozinka") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("password_input"),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Broj mobitela") },
                modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(text = "Grad (Prebivalište)") },
                modifier = Modifier.fillMaxWidth().testTag("city_input"),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                dbHelper.registerUser(name, email, password, phone, city)
                            }
                            withContext(Dispatchers.Main) {
                                if (result != -1L) {
                                    Toast.makeText(context, "Registracija uspješna!", Toast.LENGTH_SHORT).show()
                                    onRegistrationSuccess()
                                } else {
                                    Toast.makeText(context, "Greška: Email je možda već zauzet.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Popunite obavezna polja (Ime, Email, Lozinka).", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("register_button"),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "REGISTRIRAJ SE")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.testTag("reg_back_button"),
            ) {
                Text(text = "Odustani")
            }
        }
    }
}
