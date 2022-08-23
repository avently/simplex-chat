package chat.simplex.app.ui.theme

import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class DefaultThemes {
  AUTO, DARK, LIGHT
}

enum class ThemeColorComponent {
  PRIMARY,
  PRIMARY_VARIANT,
  SECONDARY,
  SECONDARY_VARIANT,
  BACKGROUND,
  SURFACE,
  ERROR,
  ON_PRIMARY,
  ON_SECONDARY,
  ON_BACKGROUND,
  ON_SURFACE,
  ON_ERROR;

  fun colorFromComponent(theme: CustomTheme): Int =
    when (this) {
      PRIMARY -> theme.colors.primary
      PRIMARY_VARIANT -> theme.colors.primaryVariant
      SECONDARY -> theme.colors.secondary
      SECONDARY_VARIANT -> theme.colors.secondaryVariant
      BACKGROUND -> theme.colors.background
      SURFACE -> theme.colors.surface
      ERROR -> theme.colors.error
      ON_PRIMARY -> theme.colors.onPrimary
      ON_SECONDARY -> theme.colors.onSecondary
      ON_BACKGROUND -> theme.colors.onBackground
      ON_SURFACE -> theme.colors.onSurface
      ON_ERROR -> theme.colors.onError
    }

  fun updateColorFromComponent(color: Int, theme: CustomTheme): CustomTheme = theme.copy(
    colors =
    when (this) {
      PRIMARY -> theme.colors.copy(primary = color)
      PRIMARY_VARIANT -> theme.colors.copy(primaryVariant = color)
      SECONDARY -> theme.colors.copy(secondary = color)
      SECONDARY_VARIANT -> theme.colors.copy(secondaryVariant = color)
      BACKGROUND -> theme.colors.copy(background = color)
      SURFACE -> theme.colors.copy(surface = color)
      ERROR -> theme.colors.copy(error = color)
      ON_PRIMARY -> theme.colors.copy(onPrimary = color)
      ON_SECONDARY -> theme.colors.copy(onSecondary = color)
      ON_BACKGROUND -> theme.colors.copy(onBackground = color)
      ON_SURFACE -> theme.colors.copy(onSurface = color)
      ON_ERROR -> theme.colors.copy(onError = color)
    }
  )
}

val DarkColorPalette = darkColors(
  primary = SimplexBlue,  // If this value changes also need to update #0088ff in string resource files
  primaryVariant = SimplexGreen,
  secondary = DarkGray,
  //  background = Color.Black,
  //  surface = Color.Black,
  //  background = Color(0xFF121212),
  //  surface = Color(0xFF121212),
  //  error = Color(0xFFCF6679),
  onBackground = Color(0xFFFFFBFA),
  onSurface = Color(0xFFFFFBFA),
  //  onError: Color = Color.Black,
)
val LightColorPalette = lightColors(
  primary = SimplexBlue,  // If this value changes also need to update #0088ff in string resource files
  primaryVariant = SimplexGreen,
  secondary = LightGray,
  //  background = Color.White,
  //  surface = Color.White
  //  onPrimary = Color.White,
  //  onSecondary = Color.Black,
  //  onBackground = Color.Black,
  //  onSurface = Color.Black,
)

@kotlinx.serialization.Serializable
data class CustomTheme(
  val colors: CustomThemeColors,
  val name: String
)

// Other colors such as Color.Unspecified.toArgb() can make background transparent unrecoverably
private const val DEFAULT_BLANK_COLOR: Int = -12345678

@kotlinx.serialization.Serializable
data class CustomThemeColors(
  // Every new color should have default value `0x00` which can be replaced later from default themes
  val primary: Int = DEFAULT_BLANK_COLOR,
  val primaryVariant: Int = DEFAULT_BLANK_COLOR,
  val secondary: Int = DEFAULT_BLANK_COLOR,
  val secondaryVariant: Int = DEFAULT_BLANK_COLOR,
  val background: Int = DEFAULT_BLANK_COLOR,
  val surface: Int = DEFAULT_BLANK_COLOR,
  val error: Int = DEFAULT_BLANK_COLOR,
  val onPrimary: Int = DEFAULT_BLANK_COLOR,
  val onSecondary: Int = DEFAULT_BLANK_COLOR,
  val onBackground: Int = DEFAULT_BLANK_COLOR,
  val onSurface: Int = DEFAULT_BLANK_COLOR,
  val onError: Int = DEFAULT_BLANK_COLOR,
  val isLight: Boolean
) {
  private fun colorOrDefault(color: Color, default: () -> Color): Color =
    if (color.toArgb() == DEFAULT_BLANK_COLOR) default() else color

  fun toMaterialColors(): Colors =
    if (isLight)
      LightColorPalette.copy(
        primary = colorOrDefault(Color(primary)) { LightColorPalette.primary },
        primaryVariant = colorOrDefault(Color(primaryVariant)) { LightColorPalette.primaryVariant },
        secondary = colorOrDefault(Color(secondary)) { LightColorPalette.secondary },
        secondaryVariant = colorOrDefault(Color(secondaryVariant)) { LightColorPalette.secondaryVariant },
        background = colorOrDefault(Color(background)) { LightColorPalette.background },
        surface = colorOrDefault(Color(surface)) { LightColorPalette.surface },
        error = colorOrDefault(Color(error)) { LightColorPalette.error },
        onPrimary = colorOrDefault(Color(onPrimary)) { LightColorPalette.onPrimary },
        onSecondary = colorOrDefault(Color(onSecondary)) { LightColorPalette.onSecondary },
        onBackground = colorOrDefault(Color(onBackground)) { LightColorPalette.onBackground },
        onSurface = colorOrDefault(Color(onSurface)) { LightColorPalette.onSurface },
        onError = colorOrDefault(Color(onError)) { LightColorPalette.onError },
      )
    else
      DarkColorPalette.copy(
        primary = colorOrDefault(Color(primary)) { DarkColorPalette.primary },
        primaryVariant = colorOrDefault(Color(primaryVariant)) { DarkColorPalette.primaryVariant },
        secondary = colorOrDefault(Color(secondary)) { DarkColorPalette.secondary },
        secondaryVariant = colorOrDefault(Color(secondaryVariant)) { DarkColorPalette.secondaryVariant },
        background = colorOrDefault(Color(background)) { DarkColorPalette.background },
        surface = colorOrDefault(Color(surface)) { DarkColorPalette.surface },
        error = colorOrDefault(Color(error)) { DarkColorPalette.error },
        onPrimary = colorOrDefault(Color(onPrimary)) { DarkColorPalette.onPrimary },
        onSecondary = colorOrDefault(Color(onSecondary)) { DarkColorPalette.onSecondary },
        onBackground = colorOrDefault(Color(onBackground)) { DarkColorPalette.onBackground },
        onSurface = colorOrDefault(Color(onSurface)) { DarkColorPalette.onSurface },
        onError = colorOrDefault(Color(onError)) { DarkColorPalette.onError },
      )

  companion object {
    fun fromColors(colors: Colors): CustomThemeColors =
      CustomThemeColors(
        primary = colors.primary.toArgb(),
        primaryVariant = colors.primaryVariant.toArgb(),
        secondary = colors.secondary.toArgb(),
        secondaryVariant = colors.secondaryVariant.toArgb(),
        background = colors.background.toArgb(),
        surface = colors.surface.toArgb(),
        error = colors.error.toArgb(),
        onPrimary = colors.onPrimary.toArgb(),
        onSecondary = colors.onSecondary.toArgb(),
        onBackground = colors.onBackground.toArgb(),
        onSurface = colors.onSurface.toArgb(),
        onError = colors.onError.toArgb(),
        isLight = colors.isLight,
      )
  }
}
