package com.example.domi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class Animal(
    val id: Int,
    val name: String,
    val breed: String,
    val age: String, // Za prikaz (npr. "8 godina")
    val ageCategory: String, // Za filter (npr. "Senior")
    val gender: String,
    val healthStatus: String,
    val isVaccinated: Boolean,
    val isNeutered: Boolean,
    val isMicrochipped: Boolean,
    val description: String,
    val imageRes: Int?,
    val shelterName: String = "Sklonište Dumovec",
    val imageUri: String? = null,
    val type: String = "Pas"
)

object AnimalRepository {
    val animals = listOf(
        Animal(1, "Darcy", "Njemački prepeličar", "8 godina", "Senior", "Ženski", "Zdrav", true, true, true, "Darcy je veseo i zaigran pas koji traži novi dom. Voli duge šetnje i igru s lopticom. Dobro se slaže s drugim psima i djecom.", R.drawable.darcy, "Sklonište Dumovec", type = "Pas"),
        Animal(2, "Luna", "Bosanski barak", "1 godina", "Adolescent", "Ženski", "Zdrava", true, true, true, "Luna je mirna i privržena ženka. Obožava maženje i vrlo je poslušna. Idealna za obitelj sa stanom.", R.drawable.bari, "Sklonište Dumovec", type = "Pas"),
        Animal(3, "Maks", "Maine coon", "4 godine", "Odrasli", "Muški", "Zdrav", true, false, true, "Maks je samostalan mačak koji dopušta da ga se mazi kada on to želi, ali uvijek će biti uz Vas i suptilno Vam pokazivati koliko Vas voli.", R.drawable.img, "Azil Prijatelji Čakovec", type = "Mačka"),
        Animal(4, "Bella", "Beagle", "3 godine", "Odrasli", "Ženski", "Zdrava", true, true, true, "Bella je prava mala dama. Ne linja se i vrlo je umiljata. Voli biti u centru pažnje i ići u šetnje gradom.", R.drawable.bella, "Sklonište Žarkovica", type = "Pas"),
        Animal(5, "Rex", "Mješanac", "5 godina", "Odrasli", "Muški", "Lakša ozljeda noge", true, true, true, "Rex je snažan pas velikog srca. Iako izgleda opasno, on je prava maza prema ljudima koje poznaje.", R.drawable.apas, "Sklonište za pse Osijek", type = "Pas")
    )

    fun getAnimalById(id: Int): Animal? {
        return animals.find { it.id == id }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    animals: List<Animal>,
    isAdmin: Boolean,
    onDelete: (Int) -> Unit,
    onAnimalClick: (Animal) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var selShelter by remember { mutableStateOf("Sve") }
    var selGender by remember { mutableStateOf("Svi") }
    var selAge by remember { mutableStateOf("Sve") }
    var selType by remember { mutableStateOf("Sve") }
    var selVaccinated by remember { mutableStateOf("Sve") }
    var selNeutered by remember { mutableStateOf("Sve") }
    var selMicrochipped by remember { mutableStateOf("Sve") }
    
    var animalToDelete by remember { mutableStateOf<Int?>(null) }

    val filtered = remember(animals, selShelter, selGender, selAge, selType, selVaccinated, selNeutered, selMicrochipped) {
        animals.filter { a ->
            (selShelter == "Sve" || a.shelterName == selShelter) &&
            (selGender == "Svi" || a.gender == selGender) &&
            (selAge == "Sve" || a.ageCategory == selAge) &&
            (selType == "Sve" || a.type == selType) &&
            (selVaccinated == "Sve" || (selVaccinated == "Da" && a.isVaccinated) || (selVaccinated == "Ne" && !a.isVaccinated)) &&
            (selNeutered == "Sve" || (selNeutered == "Da" && a.isNeutered) || (selNeutered == "Ne" && !a.isNeutered)) &&
            (selMicrochipped == "Sve" || (selMicrochipped == "Da" && a.isMicrochipped) || (selMicrochipped == "Ne" && !a.isMicrochipped))
        }
    }

    if (animalToDelete != null) {
        AlertDialog(
            onDismissRequest = { animalToDelete = null },
            title = { Text("Potvrda brisanja", fontWeight = FontWeight.Bold) },
            text = { Text("Jeste li sigurni da želite trajno obrisati ovog ljubimca?") },
            confirmButton = {
                Button(onClick = { onDelete(animalToDelete!!); animalToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Obriši") }
            },
            dismissButton = { TextButton(onClick = { animalToDelete = null }) { Text("Odustani") } }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nema rezultata za odabrane filtere.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { animal ->
                    Box {
                        AnimalCard(animal) { onAnimalClick(animal) }
                        if (isAdmin) {
                            IconButton(onClick = { animalToDelete = animal.id }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(MaterialTheme.colorScheme.errorContainer, CircleShape).size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Obriši", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(onClick = { showFilterSheet = true }, containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtriraj")
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text("Filtriraj ljubimce", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                FilterDropdown("Vrsta", listOf("Sve", "Pas", "Mačka"), selType) { selType = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Sklonište", listOf("Sve", "Sklonište Dumovec", "Azil Prijatelji Čakovec", "Sklonište za pse Osijek", "Sklonište za životinje Rijeka", "Azil Šapa Slavonski Brod", "Sklonište Žarkovica Dubrovnik"), selShelter) { selShelter = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Spol", listOf("Svi", "Muški", "Ženski"), selGender) { selGender = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Dob", listOf("Sve", "Mladunče", "Adolescent", "Odrasli", "Senior"), selAge) { selAge = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Cijepljen/a", listOf("Sve", "Da", "Ne"), selVaccinated) { selVaccinated = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Kastriran/a", listOf("Sve", "Da", "Ne"), selNeutered) { selNeutered = it }
                Spacer(modifier = Modifier.height(12.dp))
                FilterDropdown("Čipiran/a", listOf("Sve", "Da", "Ne"), selMicrochipped) { selMicrochipped = it }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { selShelter = "Sve"; selGender = "Svi"; selAge = "Sve"; selType = "Sve"; selVaccinated = "Sve"; selNeutered = "Sve"; selMicrochipped = "Sve" }, modifier = Modifier.fillMaxWidth()) { Text("PONIŠTI FILTERE") }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false }) }
        }
    }
}

@Composable
fun AnimalCard(animal: Animal, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                if (animal.imageUri != null) {
                    AsyncImage(model = animal.imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else if (animal.imageRes != null) {
                    Image(painter = painterResource(id = animal.imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = animal.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = animal.breed, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${animal.gender}, ${animal.age}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
