package chat.simplex.app.ui.theme

import chat.simplex.app.R
import chat.simplex.app.SimplexApp
import chat.simplex.app.model.AppPreferences
import chat.simplex.app.model.json
import chat.simplex.app.views.helpers.generalGetString
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString

object ThemeManager {
  private val appPrefs: AppPreferences by lazy {
    AppPreferences(SimplexApp.context)
  }

  fun currentTheme(darkTheme: Boolean): CustomTheme {
    var themeName = appPrefs.currentTheme.get()!!
    val autoThemeColors = if (darkTheme) DarkColorPalette else LightColorPalette
    var colors = when (themeName) {
      DefaultThemes.AUTO.name -> autoThemeColors
      DefaultThemes.DARK.name -> DarkColorPalette
      DefaultThemes.LIGHT.name -> LightColorPalette
      else -> null
    }

    if (colors == null) {
      val themes = customThemes()
      colors = themes.firstOrNull { it.name == themeName }?.colors?.toMaterialColors()
      // Should never happen
      if (colors == null) {
        colors = autoThemeColors
        themeName = DefaultThemes.AUTO.name
        appPrefs.currentTheme.set(themeName)
      }
    }
    return CustomTheme(colors = CustomThemeColors.fromColors(colors), name = themeName)
  }

  fun customThemes(): List<CustomTheme> =
    json.decodeFromString(ListSerializer(CustomTheme.serializer()), appPrefs.customThemes.get()!!)

  // theme, localized name
  fun allThemes(darkTheme: Boolean): List<Pair<CustomTheme, String>> {
    val allThemes = ArrayList<Pair<CustomTheme, String>>()
    allThemes.add(
      Pair(
        CustomTheme(
          name = DefaultThemes.AUTO.name,
          colors = CustomThemeColors.fromColors(if (darkTheme) DarkColorPalette else LightColorPalette)
        ),
        generalGetString(R.string.theme_auto)
      )
    )
    allThemes.add(
      Pair(
        CustomTheme(
          name = DefaultThemes.LIGHT.name,
          colors = CustomThemeColors.fromColors(LightColorPalette)
        ),
        generalGetString(R.string.theme_light)
      )
    )
    allThemes.add(
      Pair(
        CustomTheme(
          name = DefaultThemes.DARK.name,
          colors = CustomThemeColors.fromColors(DarkColorPalette)
        ),
        generalGetString(R.string.theme_dark)
      )
    )
    allThemes.addAll(customThemes().map { Pair(it, it.name) })
    return allThemes
  }

  fun generateNameFromTheme(): String {
    val themes = customThemes()
    if (themes.isEmpty()) return "#1"
    else {
      val last = themes.findLast { it.name.contains(Regex("^#\\d*$")) }
      return if (last != null)
        "#" + ((last.name.replace("#", "").toIntOrNull() ?: 0) + 1)
      else "#1"
    }
  }

  fun applyTheme(name: String, darkTheme: Boolean) {
    appPrefs.currentTheme.set(name)
    CurrentTheme.value = currentTheme(darkTheme)
  }

  private fun autoApplyThemeIfNeeded(darkTheme: Boolean) {
    val currentTheme = appPrefs.currentTheme.get()!!
    // Theme is from the default list of themes, everything is fine
    if (currentTheme in DefaultThemes.values().map { it.name }) return
    val themes = customThemes()
    if (currentTheme !in themes.map { it.name }) {
      // Select default AUTO theme if user removed prev theme, for example
      applyTheme(DefaultThemes.AUTO.name, darkTheme)
    }
  }

  fun addCustomTheme(customTheme: CustomTheme, apply: Boolean = false) {
    val themes = ArrayList(customThemes())
    themes.add(customTheme)
    appPrefs.customThemes.set(json.encodeToString(themes))
    if (apply) applyTheme(customTheme.name, !customTheme.colors.isLight)
  }

  fun editCustomTheme(oldName: String, customTheme: CustomTheme, apply: Boolean = false) {
    val themes = ArrayList(customThemes())
    val index = themes.indexOfFirst { it.name == oldName }
    if (index == -1) return
    themes.removeAt(index)
    themes.add(index, customTheme)
    appPrefs.customThemes.set(json.encodeToString(themes))
    if (apply) applyTheme(customTheme.name, !customTheme.colors.isLight)
  }

  fun removeCustomTheme(name: String) {
    val themes = ArrayList(customThemes())
    val toRemove = themes.firstOrNull { it.name == name }
    themes.remove(toRemove)
    appPrefs.customThemes.set(json.encodeToString(themes))
    if (toRemove != null)
      autoApplyThemeIfNeeded(!toRemove.colors.isLight)
  }
}
