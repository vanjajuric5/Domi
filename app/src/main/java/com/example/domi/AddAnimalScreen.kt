package com.example.domi

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimalScreen(onBack: () -> Unit, onAnimalAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val shelters = listOf(
        "Sklonište Dumovec",
        "Azil Prijatelji Čakovec",
        "Sklonište za pse Osijek",
        "Sklonište za životinje Rijeka",
        "Azil Šapa Slavonski Brod",
        "Sklonište Žarkovica Dubrovnik"
    )
    var selectedShelter by remember { mutableStateOf(shelters[0]) }

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val scope = rememberCoroutineScope()

    // ... (rest of the code)

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Selection Area
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        TextButton(onClick = { launcher.launch("image/*") }) {
            Text(if (selectedImageUri == null) "Odaberi sliku" else "Promijeni sliku")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ime ljubimca") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Pasmina") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Dob") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Shelter Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedShelter,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sklonište") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                shelters.forEach { shelter ->
                    DropdownMenuItem(
                        text = { Text(shelter) },
                        onClick = {
                            selectedShelter = shelter
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("O ljubimcu") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            dbHelper.addAnimal(
                                name = name,
                                breed = breed,
                                age = age,
                                description = description,
                                imageRes = R.drawable.apas, // Fallback if no uri
                                shelterName = selectedShelter,
                                imageUri = selectedImageUri?.toString()
                            )
                        }
                        if (result != -1L) {
                            Toast.makeText(context, "Ljubimac dodan!", Toast.LENGTH_SHORT).show()
                            onAnimalAdded()
                        } else {
                            Toast.makeText(context, "Greška pri dodavanju.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Ime je obavezno.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("DODAJ LJUBIMCA")
        }
        
        TextButton(onClick = onBack) {
            Text("Odustani")
        }
    }
}
