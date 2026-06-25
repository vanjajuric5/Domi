package com.example.domi

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    
    var name by remember { mutableStateOf(value = "") }
    var phone by remember { mutableStateOf(value = "") }
    var city by remember { mutableStateOf(value = "") }

    LaunchedEffect(userEmail) {
        val details = withContext(Dispatchers.IO) {
            dbHelper.getUserDetails(userEmail)
        }
        if (details != null) {
            name = details.name
            phone = details.phone
            city = details.city
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ime i prezime") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Broj mobitela") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Grad") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            dbHelper.updateUser(userEmail, name, phone, city)
                        }
                        withContext(Dispatchers.Main) {
                            if (result > 0) {
                                Toast.makeText(context, "Profil ažuriran!", Toast.LENGTH_SHORT).show()
                                onUpdateSuccess(name)
                            } else {
                                Toast.makeText(context, "Greška pri ažuriranju.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Ime ne može biti prazno.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("SPREMI PROMJENE")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBack) {
            Text("Odustani")
        }
    }
}
