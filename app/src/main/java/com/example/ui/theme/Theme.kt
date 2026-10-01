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
  primary = AccentSubtleBlue,
  onPrimary = DarkNavyText,
  primaryContainer = Color(0xFF0369A1),
  onPrimaryContainer = Color.White,
  secondary = SoftBlueSecondary,
  onSecondary = DarkNavyText,
  secondaryContainer = Color(0xFF1E3A8A),
  onSecondaryContainer = Color.White,
  tertiary = WbGold,
  onTertiary = Color.Black,
  background = DarkNavyBackground,
  surface = DarkNavySurface,
  surfaceVariant = DarkNavySurfaceVariant,
  onBackground = DarkTextPrimary,
  onSurface = DarkTextPrimary,
  onSurfaceVariant = DarkTextSecondary,
  outline = Color(0xFF475569),
  error = AlertRed
)

private val LightColorScheme = lightColorScheme(
  primary = SkyBluePrimary,
  onPrimary = Color.White,
  primaryContainer = SkyBlueContainer,
  onPrimaryContainer = OnSkyBlueContainer,
  secondary = SoftBlueSecondary,
  onSecondary = DarkNavyText,
  secondaryContainer = SoftBlueContainer,
  onSecondaryContainer = DarkNavyText,
  tertiary = WbGold,
  onTertiary = Color.White,
  tertiaryContainer = WbGoldContainer,
  onTertiaryContainer = Color(0xFF78350F),
  background = BackgroundBlueWhite,
  surface = CardWhite,
  surfaceVariant = SoftBlueContainer,
  onBackground = DarkNavyText,
  onSurface = DarkNavyText,
  onSurfaceVariant = SlateGreyText,
  outline = BorderSoftBlue,
  error = AlertRed
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Soft Light Sky Blue theme by default as requested
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
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
