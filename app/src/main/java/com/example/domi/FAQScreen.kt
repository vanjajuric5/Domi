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

        Text(
            text = "Često postavljana pitanja",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        FAQItem(
            question = "Kako mogu udomiti ljubimca?",
            answer = "Pregledajte dostupne ljubimce, otvorite njihov profil i kliknite na gumb 'Udomi me'. Nakon ispunjavanja kratkog obrasca, sklonište ili azil će vas kontaktirati s daljnjim informacijama."
        )

        FAQItem(
            question = "Je li udomljavanje besplatno?",
            answer = "U većini slučajeva da. Međutim, neka skloništa mogu zatražiti simboličnu naknadu koja pokriva troškove cijepljenja, čipiranja ili veterinarske skrbi."
        )

        FAQItem(
            question = "Kako znam je li ljubimac još uvijek dostupan?",
            answer = "Status svakog ljubimca redovito se ažurira. Ako je životinja već udomljena, više se neće prikazivati kao dostupna za udomljavanje."
        )

        FAQItem(
            question = "Mogu li upoznati ljubimca prije odluke o udomljavanju?",
            answer = "Da. Nakon što pošaljete zahtjev za udomljavanje, sklonište će vam pružiti informacije o mogućem susretu i upoznavanju životinje."
        )

        FAQItem(
            question = "Što ako nakon udomljavanja naiđem na poteškoće?",
            answer = "Preporučujemo da se prvo obratite skloništu iz kojeg je ljubimac udomljen. Njihovo iskustvo može pomoći u prilagodbi i rješavanju eventualnih problema."
        )

        FAQItem(
            question = "Mogu li vratiti ljubimca?",
            answer = "Udomljavanje je dugoročna odgovornost, no ako nastanu ozbiljne i nepredviđene okolnosti, obratite se skloništu kako biste pronašli najbolje rješenje za životinju."
        )

        FAQItem(
            question = "Kako mogu postati volonter?",
            answer = "Mnogim skloništima potrebna je pomoć u šetnji pasa, brizi za životinje i organizaciji događaja. Informacije možete pronaći na stranicama pojedinih skloništa ili nas kontaktirati za više detalja."
        )

        FAQItem(
            question = "Kako mogu prijaviti problem u aplikaciji?",
            answer = "Ako primijetite grešku ili imate prijedlog za poboljšanje, javite nam se putem kontakt obrasca ili e-mail adrese navedene u aplikaciji."
        )

        FAQItem(
            question = "Je li korištenje aplikacije besplatno?",
            answer = "Da. Pregledavanje ljubimaca i slanje zahtjeva za udomljavanje potpuno je besplatno."
        )

        FAQItem(
            question = "Zašto je udomljavanje bolje od kupnje ljubimca?",
            answer = "Udomljavanjem pružate drugu priliku životinji koja traži dom. Osim što pomažete jednoj životinji, podupirete i rad skloništa te promičete odgovorno vlasništvo."
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
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