package chat.simplex.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow

val CurrentTheme: MutableStateFlow<CustomTheme> = MutableStateFlow(ThemeManager.currentTheme(true))

@Composable
fun isInDarkTheme(): Boolean {
  return !CurrentTheme.collectAsState().value.colors.isLight
}

@Composable
fun SimpleXTheme(darkTheme: Boolean? = null, content: @Composable () -> Unit) {
  LaunchedEffect(darkTheme) {
    // For preview
    if (darkTheme != null)
      CurrentTheme.value = ThemeManager.currentTheme(darkTheme)
  }
  val systemDark = isSystemInDarkTheme()
  LaunchedEffect(systemDark) {
    if (CurrentTheme.value.name == DefaultThemes.AUTO.name && CurrentTheme.value.colors.isLight == systemDark) {
      // Change active colors from light to dark and back based on system theme
      ThemeManager.applyTheme(DefaultThemes.AUTO.name, systemDark)
    }
  }
  val theme by CurrentTheme.collectAsState()
  MaterialTheme(
    colors = theme.colors.toMaterialColors(),
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}