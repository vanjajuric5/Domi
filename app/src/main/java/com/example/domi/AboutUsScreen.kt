package com.example.domi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutUsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tko smo mi?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Domi je aplikacija posvećena udomljavanju životinja i povezivanju ljubimaca iz azila s njihovim novim vlasnicima. Naš cilj je olakšati proces udomljavanja i osigurati svakoj životinji topao dom.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Naša misija",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vjerujemo da svaki pas, mačka i ostali stanovnici azila zaslužuju drugu priliku. Kroz Domi aplikaciju, želimo smanjiti broj napuštenih životinja i promovirati odgovorno vlasništvo.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
