package com.example.domi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isNightMode by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf("list") }

            DomiTheme(darkTheme = isNightMode) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == "list",
                                onClick = { currentScreen = "list" },
                                icon = { Icon(painterResource(R.drawable.ic_animals), contentDescription = null) },
                                label = { Text("Životinje") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == "settings",
                                onClick = { currentScreen = "settings" },
                                icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = null) },
                                label = { Text("Postavke") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "list" -> AnimalListScreen()
                            "settings" -> SettingsScreen(
                                isNightMode = isNightMode,
                                onNightModeChange = { isNightMode = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
