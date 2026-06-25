package com.example.domi

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnimalScreen(onBack: () -> Unit, onAnimalAdded: () -> Unit) {
    var name by remember { mutableStateOf(value = "") }
    var breed by remember { mutableStateOf(value = "") }
    var exactAge by remember { mutableStateOf(value = "") }
    var description by remember { mutableStateOf(value = "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(value = null) }
    var health by remember { mutableStateOf(value = "Zdrav/a") }
    var isVaccinated by remember { mutableStateOf(value = true) }
    var isNeutered by remember { mutableStateOf(value = true) }
    var isMicrochipped by remember { mutableStateOf(value = true) }

    // Dropdowns
    var typeExp by remember { mutableStateOf(value = false) }
    val types = listOf("Pas", "Mačka")
    var selType by remember { mutableStateOf(value = types[0]) }

    var ageExp by remember { mutableStateOf(value = false) }
    val ageCats = listOf("Mladunče", "Adolescent", "Odrasli", "Senior")
    var selAgeCat by remember { mutableStateOf(value = ageCats[2]) }

    var shelterExp by remember { mutableStateOf(value = false) }
    val shelters = listOf("Sklonište Dumovec", "Azil Prijatelji Čakovec", "Sklonište za pse Osijek", "Sklonište za životinje Rijeka", "Azil Šapa Slavonski Brod", "Sklonište Žarkovica Dubrovnik")
    var selShelter by remember { mutableStateOf(value = shelters[0]) }

    var genExp by remember { mutableStateOf(value = false) }
    val genders = listOf("Muški", "Ženski")
    var selGender by remember { mutableStateOf(value = genders[0]) }

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? -> selectedImageUri = uri }

    Column(modifier = Modifier.fillMaxSize().padding(26.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(150.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            if (selectedImageUri != null) {
                AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(painter = painterResource(id = R.drawable.ic_profile), contentDescription = null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        TextButton(onClick = { launcher.launch("image/*") }) { Text(text = if (selectedImageUri == null) "Odaberi sliku" else "Promijeni sliku") }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(text = "Ime ljubimca") }, modifier = Modifier.fillMaxWidth().testTag("animal_name_input"))
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = typeExp, onExpandedChange = { typeExp = !typeExp }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = selType, onValueChange = {}, readOnly = true, label = { Text(text = "Vrsta") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExp) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth())
            ExposedDropdownMenu(expanded = typeExp, onDismissRequest = { typeExp = false }) {
                types.forEach { t -> DropdownMenuItem(text = { Text(text = t) }, onClick = { selType = t; typeExp = false }) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text(text = "Pasmina") }, modifier = Modifier.fillMaxWidth().testTag("animal_breed_input"))
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = exactAge, onValueChange = { exactAge = it }, label = { Text(text = "Stvarna dob (npr. 8 godina)") }, modifier = Modifier.fillMaxWidth().testTag("animal_age_input"))
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = ageExp, onExpandedChange = { ageExp = !ageExp }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = selAgeCat, onValueChange = {}, readOnly = true, label = { Text(text = "Kategorija za filter") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageExp) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth())
            ExposedDropdownMenu(expanded = ageExp, onDismissRequest = { ageExp = false }) {
                ageCats.forEach { a -> DropdownMenuItem(text = { Text(text = a) }, onClick = { selAgeCat = a; ageExp = false }) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = genExp, onExpandedChange = { genExp = !genExp }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = selGender, onValueChange = {}, readOnly = true, label = { Text(text = "Spol") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genExp) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth())
            ExposedDropdownMenu(expanded = genExp, onDismissRequest = { genExp = false }) {
                genders.forEach { g -> DropdownMenuItem(text = { Text(text = g) }, onClick = { selGender = g; genExp = false }) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = shelterExp, onExpandedChange = { shelterExp = !shelterExp }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = selShelter, onValueChange = {}, readOnly = true, label = { Text(text = "Sklonište") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shelterExp) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth())
            ExposedDropdownMenu(expanded = shelterExp, onDismissRequest = { shelterExp = false }) {
                shelters.forEach { s -> DropdownMenuItem(text = { Text(text = s) }, onClick = { selShelter = s; shelterExp = false }) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = health, onValueChange = { health = it }, label = { Text(text = "Zdravstveno stanje") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = isVaccinated, onCheckedChange = { isVaccinated = it }); Text(text = "Cijepljen/a") }
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = isNeutered, onCheckedChange = { isNeutered = it }); Text(text = "Kastriran/a") }
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = isMicrochipped, onCheckedChange = { isMicrochipped = it }); Text(text = "Čipiran/a") }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(text = "O ljubimcu") }, modifier = Modifier.fillMaxWidth().testTag("animal_description_input"), minLines = 4)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            dbHelper.addAnimal(
                                name = name,
                                breed = breed, 
                                age = exactAge, 
                                ageCategory = selAgeCat,
                                gender = selGender, 
                                health = health, 
                                isVaccinated = isVaccinated, 
                                isNeutered = isNeutered, 
                                isMicrochipped = isMicrochipped, 
                                description = description, 
                                imageRes = null, 
                                shelterName = selShelter, 
                                type = selType, 
                                imageUri = selectedImageUri?.toString()
                            )
                        }
                        withContext(Dispatchers.Main) {
                            if (result != -1L) {
                                Toast.makeText(context, "Ljubimac dodan!", Toast.LENGTH_SHORT).show()
                                onAnimalAdded()
                            } else {
                                Toast.makeText(context, "Greška pri dodavanju.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Ime je obavezno.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("add_animal_submit_button"),
            shape = RoundedCornerShape(12.dp)
        ) { Text(text = "DODAJ LJUBIMCA") }
        TextButton(onClick = onBack) { Text(text = "Odustani") }
    }
}
