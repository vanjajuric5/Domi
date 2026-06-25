package com.example.domi

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AnimalDetailScreen(animal: Animal, isAdmin: Boolean, userName: String, userEmail: String) {
    var showForm by remember { mutableStateOf(value = false) }
    
    if (showForm) {
        AdoptionFormScreen(animalName = animal.name, userName = userName, userEmail = userEmail) { 
            showForm = false 
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (animal.imageUri != null) {
                    AsyncImage(
                        model = animal.imageUri,
                        contentDescription = "Slika ljubimca",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    animal.imageRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = "Slika ljubimca",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.3f))) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prethodna", tint = Color.White)
                    }
                    IconButton(onClick = { }, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.3f))) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Sljedeća", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "${animal.name} (${animal.type})", fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = MaterialTheme.typography.displayLarge.fontFamily)
                    Text(text = "${animal.breed}, ${animal.age} (${animal.gender})", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = animal.shelterName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(label = "Cijepljen", status = animal.isVaccinated)
                        StatusBadge(label = "Kastriran", status = animal.isNeutered)
                        StatusBadge(label = "Čipiran", status = animal.isMicrochipped)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Zdravlje: ${animal.healthStatus}", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = "O ljubimcu", fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = MaterialTheme.typography.displayLarge.fontFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = animal.description, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }

            if (!isAdmin) {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showForm = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp).testTag("adopt_button"),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("UDOMI ME", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatusBadge(label: String, status: Boolean) {
    Surface(color = if (status) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = if (status) Icons.Default.Check else Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (status) Color(0xFF2E7D32) else Color(0xFFC62828))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (status) Color(0xFF2E7D32) else Color(0xFFC62828))
        }
    }
}

@Composable
fun AdoptionFormScreen(animalName: String, userName: String, userEmail: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf(value = "") }
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context.applicationContext) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Obrazac za udomljavanje", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Ljubimac: $animalName", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Zašto želite udomiti ovog ljubimca?") },
            modifier = Modifier.fillMaxWidth().testTag("adoption_message_input"),
            minLines = 5
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (message.isNotEmpty()) {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) { dbHelper.addRequest(animalName, userName, userEmail, message) }
                        withContext(Dispatchers.Main) {
                            if (result != -1L) {
                                Toast.makeText(context, "Zahtjev poslan!", Toast.LENGTH_LONG).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Greška pri slanju zahtjeva.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("send_adoption_request_button")
        ) { Text("POŠALJI ZAHTJEV") }
        TextButton(onClick = onBack) { Text("Odustani") }
    }
}
