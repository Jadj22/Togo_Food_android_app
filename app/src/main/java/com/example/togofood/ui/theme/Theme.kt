package com.example.togofood.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// CompositionLocal pour le vert argent (prix) - s'adapte au thème
val LocalPriceGreen = compositionLocalOf { PriceGreen }

private val DarkColorScheme = darkColorScheme(
    primary = OrangeTogoDark,
    onPrimary = Color(0xFF100F18),
    primaryContainer = OrangeTogoDarkContainer,
    onPrimaryContainer = Color(0xFF100F18),

    secondary = PurpleGrey80,
    onSecondary = Color(0xFF1D1A22),
    secondaryContainer = PurpleGrey40,
    onSecondaryContainer = Color(0xFFE8DEF8),

    tertiary = Pink80,
    onTertiary = Color(0xFF31101D),
    tertiaryContainer = Pink40,
    onTertiaryContainer = Color(0xFFFFD8E4),

    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),

    surface = Color(0xFF1E1C22),
    onSurface = Color(0xFFE6E0E9),
    surfaceContainer = Color(0xFF26242C),
    surfaceContainerHigh = Color(0xFF312F37),
    surfaceContainerHighest = Color(0xFF3C3A42),

    outline = Color(0xFF7A757F),
    outlineVariant = Color(0xFF4A464F),
    onSurfaceVariant = Color(0xFFB0BEC5),  // bleu-gris clair pour mode sombre

    inverseSurface = Color(0xFFE6E0E9),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = OrangeTogo,

    scrim = Color.Black,
    surfaceTint = OrangeTogoDark,
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeTogo,
    onPrimary = Color.White,
    primaryContainer = LightOrangeBg,
    onPrimaryContainer = Color(0xFF100F18),

    secondary = PurpleGrey40,
    onSecondary = Color.White,
    secondaryContainer = PurpleGrey80,
    onSecondaryContainer = Color(0xFF1D1A22),

    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Pink80,
    onTertiaryContainer = Color(0xFF31101D),

    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFCFCFC),
    onSurface = Color(0xFF1C1B1F),
    surfaceContainer = Color(0xFFF3F0F7),
    surfaceContainerHigh = Color(0xFFE8E5EE),
    surfaceContainerHighest = Color(0xFFDDDAE2),

    outline = GrayBorder,
    outlineVariant = GrayBorder.copy(alpha = 0.6f),
    onSurfaceVariant = GrayText,

    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF3F0F7),
    inversePrimary = OrangeTogo,

    scrim = Color.Black,
    surfaceTint = OrangeTogo,
)

@Composable
fun TogoFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    val priceGreen = if (darkTheme) PriceGreenDark else PriceGreen

    CompositionLocalProvider(LocalPriceGreen provides priceGreen) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}