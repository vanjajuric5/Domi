package com.example.domi

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AutumnPrimaryDark,
    secondary = AutumnSecondaryDark,
    tertiary = AutumnTertiaryDark,
    background = AutumnBackgroundDark,
    surface = AutumnSurfaceDark,
    surfaceVariant = AutumnSurfaceVariantDark,
    secondaryContainer = AutumnCardGreenDark // Zelene kartice u tamnom načinu
)

private val LightColorScheme = lightColorScheme(
    primary = AutumnPrimary,
    secondary = AutumnSecondary,
    tertiary = AutumnTertiary,
    background = AutumnBackground,
    surface = AutumnSurface,
    surfaceVariant = AutumnSurfaceVariant,
    secondaryContainer = AutumnCardGreen // Zelene kartice u svijetlom načinu
)

@Composable
fun DomiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
