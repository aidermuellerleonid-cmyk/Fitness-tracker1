package de.fitapp.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Schwarz/Blau-Farbschema (immer dunkel, unabhängig vom Systemdesign).
private val Blue = Color(0xFF2196F3)
private val BlueLight = Color(0xFF64B5F6)
private val BlueDark = Color(0xFF0D47A1)
private val NearBlack = Color(0xFF0A0C10)
private val SurfaceBlack = Color(0xFF13161C)
private val SurfaceVariantBlack = Color(0xFF1C2028)

private val DarkColors = darkColorScheme(
    primary = BlueLight,
    onPrimary = Color(0xFF00274D),
    primaryContainer = BlueDark,
    onPrimaryContainer = Color.White,
    secondary = Blue,
    onSecondary = Color.White,
    tertiary = BlueLight,
    onTertiary = Color(0xFF00274D),
    background = NearBlack,
    onBackground = Color(0xFFE3E6EB),
    surface = SurfaceBlack,
    onSurface = Color(0xFFE3E6EB),
    surfaceVariant = SurfaceVariantBlack,
    onSurfaceVariant = Color(0xFFB8C2CE),
    outline = Color(0xFF3A4250)
)

@Composable
fun FitAppTheme(content: @Composable () -> Unit) {
    // Bewusst immer dunkel (Schwarz/Blau), unabhängig vom Systemdesign.
    MaterialTheme(colorScheme = DarkColors, content = content)
}

val BrandBlueDark = BlueDark
