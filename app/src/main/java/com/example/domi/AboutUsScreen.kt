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
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Domi je više od aplikacije za udomljavanje – to je mjesto gdje započinju nova prijateljstva između ljudi i životinja koje traže svoj zauvijek dom.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Vjerujemo da svaka životinja zaslužuje ljubav, sigurnost i priliku za sretan život. Zato smo stvorili Domi kako bismo pojednostavili proces udomljavanja i povezali buduće vlasnike s ljubimcima iz skloništa i azila.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Naša platforma omogućuje pregled dostupnih životinja, upoznavanje njihovih priča i jednostavan kontakt sa skloništima. Svaki klik može biti korak prema novom početku za Vas i Vašeg budućeg najboljeg prijatelja.",
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
            text = "Naša misija je smanjiti broj napuštenih životinja i potaknuti odgovorno udomljavanje.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Želimo da svaki pas, mačka ili drugi ljubimac dobije priliku pronaći obitelj koja će ga voljeti i brinuti se o njemu. Kroz edukaciju, suradnju sa skloništima i modernu tehnologiju nastojimo učiniti proces udomljavanja jednostavnijim, transparentnijim i dostupnijim svima.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Zašto Domi?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "• Jednostavno pretraživanje životinja za udomljavanje\n" +
                    "• Brz kontakt sa skloništima i azilima\n" +
                    "• Pouzdane informacije o svakom ljubimcu\n" +
                    "• Promicanje odgovornog vlasništva i brige o životinjama",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hvala što pomažete stvarati svijet u kojem svaka životinja ima priliku pronaći svoj dom.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}