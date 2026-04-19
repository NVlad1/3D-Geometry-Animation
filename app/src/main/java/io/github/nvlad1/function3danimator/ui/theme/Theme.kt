package io.github.nvlad1.function3danimator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentColor
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.MaterialTheme as Material2Theme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme as Material3Theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Function3dAnimatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    Material2Theme(
        colors = colorScheme.toMaterial2Colors(darkTheme)
    ) {
        CompositionLocalProvider(LocalContentColor provides Material2Theme.colors.onBackground) {
            Material3Theme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}

private fun ColorScheme.toMaterial2Colors(darkTheme: Boolean) =
    if (darkTheme) {
        darkColors(
            primary = primary,
            primaryVariant = primaryContainer,
            secondary = secondary,
            secondaryVariant = secondaryContainer,
            background = background,
            surface = surface,
            error = error,
            onPrimary = onPrimary,
            onSecondary = onSecondary,
            onBackground = onBackground,
            onSurface = onSurface,
            onError = onError
        )
    } else {
        lightColors(
            primary = primary,
            primaryVariant = primaryContainer,
            secondary = secondary,
            secondaryVariant = secondaryContainer,
            background = background,
            surface = surface,
            error = error,
            onPrimary = onPrimary,
            onSecondary = onSecondary,
            onBackground = onBackground,
            onSurface = onSurface,
            onError = onError
        )
    }
