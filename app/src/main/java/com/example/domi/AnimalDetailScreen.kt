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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun AnimalDetailScreen(animal: Animal, isAdmin: Boolean, userName: String, userEmail: String) {
    var showForm by remember { mutableStateOf(false) }
    
    if (showForm) {
        AdoptionFormScreen(animalName = animal.name, userName = userName, userEmail = userEmail) { showForm = false }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                if (animal.imageUri != null) {
                    AsyncImage(
                        model = animal.imageUri,
                        contentDescription = "Slika ljubimca",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = animal.imageRes),
                        contentDescription = "Slika ljubimca",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(painterResource(id = R.drawable.ic_chevron_left), contentDescription = "Prethodna", tint = Color.White)
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(painterResource(id = R.drawable.ic_chevron_right), contentDescription = "Sljedeća", tint = Color.White)
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
                    Text(text = animal.name, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = MaterialTheme.typography.displayLarge.fontFamily)
                    Text(text = "${animal.breed}, ${animal.age}", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = animal.shelterName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }

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
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("UDOMI ME", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AdoptionFormScreen(animalName: String, userName: String, userEmail: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Obrazac za udomljavanje", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Ljubimac: $animalName", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Zašto želite udomiti ovog ljubimca?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (message.isNotEmpty()) {
                    val result = dbHelper.addRequest(animalName, userName, userEmail, message)
                    if (result != -1L) {
                        Toast.makeText(context, "Zahtjev poslan!", Toast.LENGTH_LONG).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Greška pri slanju zahtjeva.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Molimo unesite poruku.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("POŠALJI ZAHTJEV")
        }
        
        TextButton(onClick = onBack) {
            Text("Odustani")
        }
    }
}
