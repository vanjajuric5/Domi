package com.example.domi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdoptionRequestsScreen() {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var requests by remember { mutableStateOf(listOf<DatabaseHelper.AdoptionRequest>()) }
    val scope = rememberCoroutineScope()

    fun refreshRequests() {
        scope.launch {
            requests = withContext(Dispatchers.IO) {
                dbHelper.getAllRequests()
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshRequests()
    }

    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nema novih zahtjeva za udomljavanje.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requests, key = { it.id }) { request ->
                RequestCard(
                    request = request,
                    onDelete = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.deleteRequest(request.id)
                            }
                            refreshRequests()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RequestCard(request: DatabaseHelper.AdoptionRequest, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ljubimac: ${request.animalName}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Obriši zahtjev", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Korisnik: ${request.userName}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Email: ${request.userEmail}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Poruka:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = request.message,
                fontSize = 14.sp
            )
        }
    }
}
