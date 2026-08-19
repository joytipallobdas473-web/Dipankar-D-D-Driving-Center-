package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ImperialGold,            // Soothing Warm Amber Gold
    onPrimary = ObsidianBlack,
    primaryContainer = SapphireBlue,   // Muted Slate Panel
    onPrimaryContainer = ChampagneGold,
    secondary = RoyalBlue,             // Soothing Muted Teal Accent
    onSecondary = Color.White,
    tertiary = MetallicGold,           // Elegant Deep Amber
    background = ObsidianBlack,        // Low-glare Warm Dark Charcoal Canvas
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = ImperialGold
)

private val LightColorScheme = lightColorScheme(
    primary = SapphireBlue,
    onPrimary = Color.White,
    primaryContainer = ImperialGold,
    onPrimaryContainer = ObsidianBlack,
    secondary = RoyalBlue,
    onSecondary = Color.White,
    tertiary = MetallicGold,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextDark,
    outline = ImperialGold
)

@Composable
fun DDDrivingCenterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve unique Cyber Volt palette identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


