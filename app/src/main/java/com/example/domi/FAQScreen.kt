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
fun FAQScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        FAQItem(
            question = "Kako udomiti ljubimca?",
            answer = "Odaberite ljubimca s popisa, pročitajte njegove detalje i kliknite na gumb 'UDOMI ME'. Nakon toga ispunite kratki obrazac i skonište će vas kontaktirati."
        )
        FAQItem(
            question = "Je li udomljavanje besplatno?",
            answer = "Udomljavanje iz većine azila je besplatno, ali neka skloništa traže simboličnu naknadu za troškove čipiranja i cijepljenja."
        )
        FAQItem(
            question = "Mogu li vratiti ljubimca?",
            answer = "Uvijek potičemo odgovorno udomljavanje, ali ako se pojave nepredviđeni problemi, kontaktirajte azil iz kojeg je ljubimac udomljen."
        )
        FAQItem(
            question = "Kako postati volonter?",
            answer = "Informacije o volontiranju možete pronaći na stranicama pojedinih azila ili nas kontaktirajte putem emaila."
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
    }
}
