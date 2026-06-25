package com.example.domi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    val dbHelper = remember { DatabaseHelper(context.applicationContext) }
    var requests by remember { mutableStateOf(value = listOf<DatabaseHelper.AdoptionRequest>()) }
    val scope = rememberCoroutineScope()
    var requestToDelete by remember { mutableStateOf<DatabaseHelper.AdoptionRequest?>(value = null) }

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

    requestToDelete?.let { request ->
        AlertDialog(
            onDismissRequest = { requestToDelete = null },
            title = { Text(text = "Potvrda brisanja", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Jeste li sigurni da želite obrisati ovaj zahtjev?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.deleteRequest(request.id)
                            }
                            refreshRequests()
                            requestToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Obriši")
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToDelete = null }) {
                    Text(text = "Odustani")
                }
            }
        )
    }

    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nema novih zahtjeva za udomljavanje.", color = Color.Gray)
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
                    onDelete = { requestToDelete = request }
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
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Obriši zahtjev", tint = MaterialTheme.colorScheme.error)
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
