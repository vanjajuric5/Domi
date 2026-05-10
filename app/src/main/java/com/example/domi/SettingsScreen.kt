package com.example.domi

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit,
) {
    var notificationsEnabled by remember { mutableStateOf(value = true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
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
        ) { notificationsEnabled = it }

        SettingsClickableItem(
            title = "Jezik",
            subtitle = "Hrvatski",
            iconResId = R.drawable.ic_language,
        ) { /* TODO: Change Language */ }

        SettingsClickableItem(
            title = "Česta pitanja (FAQ)",
            iconResId = R.drawable.ic_qna,
        ) { /* TODO: Navigate to FAQ */ }

        SettingsClickableItem(
            title = "O nama",
            iconResId = R.drawable.ic_info,
        ) { /* TODO: Navigate to About Us */ }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Odjavi se", color = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
fun ProfileSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
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
    onCheckedChange: (Boolean) -> Unit,
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
        SettingsScreen(isNightMode = false) {}
    }
}
