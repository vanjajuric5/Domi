package com.example.domi

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Animal(
    val id: Int,
    val name: String,
    val breed: String,
    val age: String,
    val description: String,
    val imageRes: Int
)

object AnimalRepository {
    val animals = listOf(
        Animal(1, "Bobi", "Mješanac", "2 godine", "Bobi je veseo i zaigran pas koji traži novi dom. Voli duge šetnje i igru s lopticom. Dobro se slaže s drugim psima i djecom.", R.drawable.ic_profile),
        Animal(2, "Luna", "Zlatni retriver", "1 godina", "Luna je mirna i privržena ženka. Obožava maženje i vrlo je poslušna. Idealna za obitelj sa stanom.", R.drawable.ic_profile),
        Animal(3, "Maks", "Njemački ovčar", "4 godine", "Maks je odan čuvar i vrlo pametan pas. Potreban mu je aktivan vlasnik koji će s njim raditi i vježbati.", R.drawable.ic_profile),
        Animal(4, "Bella", "Pudla", "3 godine", "Bella je prava mala dama. Ne linja se i vrlo je umiljata. Voli biti u centru pažnje i ići u šetnje gradom.", R.drawable.ic_profile),
        Animal(5, "Rex", "Rotvajler", "5 godina", "Rex je snažan pas velikog srca. Iako izgleda opasno, on je prava maza prema ljudima koje poznaje.", R.drawable.ic_profile)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(onAnimalClick: (Animal) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(AnimalRepository.animals) { animal ->
            AnimalCard(animal, onClick = { onAnimalClick(animal) })
        }
    }
}

@Composable
fun AnimalCard(animal: Animal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer // Zelena boja koja se mijenja s temom
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = animal.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(text = animal.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = animal.breed, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = animal.age, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
