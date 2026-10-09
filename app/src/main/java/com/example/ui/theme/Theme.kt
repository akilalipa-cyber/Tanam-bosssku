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

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = LeafGreenContainer,
    onPrimaryContainer = OnLeafGreenContainer,
    secondary = LeafGreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = LeafGreenContainer,
    onSecondaryContainer = ForestGreenDark,
    tertiary = AmberSunAccent,
    onTertiary = Color.White,
    tertiaryContainer = AmberSunContainer,
    onTertiaryContainer = GoldenHarvest,
    background = BackgroundSoft,
    onBackground = TextPrimaryDark,
    surface = SurfaceCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryMuted,
    outline = OutlineBorder,
    error = StatusDanger,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF003915),
    primaryContainer = ForestGreenPrimary,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF003915),
    secondaryContainer = Color(0xFF25372E),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary = AmberSunAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF533C00),
    onTertiaryContainer = AmberSunContainer,
    background = DarkBackground,
    onBackground = Color(0xFFE2EBE5),
    surface = DarkSurface,
    onSurface = Color(0xFFE2EBE5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0C4B8),
    outline = Color(0xFF3E5448),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent agricultural branding
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
