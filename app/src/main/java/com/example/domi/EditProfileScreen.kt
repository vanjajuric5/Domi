package com.example.domi

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(userEmail: String, onBack: () -> Unit, onUpdateSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    LaunchedEffect(userEmail) {
        val details = withContext(Dispatchers.IO) {
            dbHelper.getUserDetails(userEmail)
        }
        if (details != null) {
            name = details.name
            phone = details.phone
            city = details.city
            experience = details.experience
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (existing fields)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ime i prezime") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Broj mobitela") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Grad") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("Iskustvo s ljubimcima") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            dbHelper.updateUser(userEmail, name, phone, city, experience)
                        }
                        if (result > 0) {
                            Toast.makeText(context, "Profil ažuriran!", Toast.LENGTH_SHORT).show()
                            onUpdateSuccess(name)
                        } else {
                            Toast.makeText(context, "Greška pri ažuriranju.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Ime ne može biti prazno.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SPREMI PROMJENE")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBack) {
            Text("Odustani")
        }
    }
}
