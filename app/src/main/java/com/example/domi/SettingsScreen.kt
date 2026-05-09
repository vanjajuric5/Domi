package com.example.domi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postavke") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Section
            ProfileSection()

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Settings Options
            SettingsToggleItem(
                title = "Noćni način rada",
                iconResId = R.drawable.ic_nightlight,
                checked = isNightMode,
                onCheckedChange = onNightModeChange
            )

            SettingsToggleItem(
                title = "Obavijesti",
                iconResId = R.drawable.ic_notifications,
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingsClickableItem(
                title = "Jezik",
                subtitle = "Hrvatski",
                iconResId = R.drawable.ic_language,
                onClick = { /* TODO: Change Language */ }
            )

            SettingsClickableItem(
                title = "Česta pitanja (QnA)",
                iconResId = R.drawable.ic_qna,
                onClick = { /* TODO: Navigate to QnA */ }
            )

            SettingsClickableItem(
                title = "O nama",
                iconResId = R.drawable.ic_info,
                onClick = { /* TODO: Navigate to About Us */ }
            )
        }
    }
}

@Composable
fun ProfileSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Placeholder for profile picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profilna slika",
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ime Prezime",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "korisnik@email.com",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { /* TODO: Edit Profile */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Uredi profil")
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    iconResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(painterResource(id = iconResId), contentDescription = null) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String? = null,
    iconResId: Int,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(painterResource(id = iconResId), contentDescription = null) },
        trailingContent = { Icon(painterResource(id = R.drawable.ic_chevron_right), contentDescription = null) },
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    DomiTheme {
        SettingsScreen(isNightMode = false, onNightModeChange = {})
    }
}
