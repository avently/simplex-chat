package chat.simplex.app.views.usersettings

import SectionItemViewSpaceBetween
import SectionSpacer
import SectionView
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import chat.simplex.app.*
import chat.simplex.app.R
import chat.simplex.app.ui.theme.*
import chat.simplex.app.views.helpers.*
import com.godaddy.android.colorpicker.*

enum class AppIcon(val resId: Int) {
  DEFAULT(R.mipmap.icon),
  DARK_BLUE(R.mipmap.icon_dark_blue),
}

@Composable
fun AppearanceView(
  showCustomModal: (@Composable (close: () -> Unit) -> Unit) -> Unit,
) {
  val appIcon = remember { mutableStateOf(findEnabledIcon()) }

  fun setAppIcon(newIcon: AppIcon) {
    if (appIcon.value == newIcon) return
    val newComponent = ComponentName(BuildConfig.APPLICATION_ID, "chat.simplex.app.MainActivity_${newIcon.name.lowercase()}")
    val oldComponent = ComponentName(BuildConfig.APPLICATION_ID, "chat.simplex.app.MainActivity_${appIcon.value.name.lowercase()}")
    SimplexApp.context.packageManager.setComponentEnabledSetting(
      newComponent,
      COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
    )

    SimplexApp.context.packageManager.setComponentEnabledSetting(
      oldComponent,
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
    )

    appIcon.value = newIcon
  }

  val darkTheme = isInDarkTheme()
  val allThemes = remember { mutableStateOf(ThemeManager.allThemes(darkTheme)) }

  AppearanceLayout(
    appIcon,
    allThemes.value,
    changeIcon = ::setAppIcon,
    createTheme = { showThemeEditor(null, darkTheme, allThemes, showCustomModal) },
    editTheme = { theme -> showThemeEditor(theme, darkTheme, allThemes, showCustomModal) },
    removeTheme = { theme ->
      ThemeManager.removeCustomTheme(theme)
      allThemes.value = ThemeManager.allThemes(darkTheme)
    },
  )
}

@Composable fun AppearanceLayout(
  icon: MutableState<AppIcon>,
  allThemes: List<Pair<CustomTheme, String>>,
  changeIcon: (AppIcon) -> Unit,
  createTheme: () -> Unit,
  editTheme: (CustomTheme) -> Unit,
  removeTheme: (String) -> Unit,
) {
  Column(
    Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      stringResource(R.string.appearance_settings),
      Modifier.padding(start = 16.dp, bottom = 24.dp),
      style = MaterialTheme.typography.h1
    )
    SectionView(stringResource(R.string.settings_section_title_icon)) {
      LazyRow {
        items(AppIcon.values().size, { index -> AppIcon.values()[index] }) { index ->
          val item = AppIcon.values()[index]
          val mipmap = ContextCompat.getDrawable(LocalContext.current, item.resId)!!
          Image(
            bitmap = mipmap.toBitmap().asImageBitmap(),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .shadow(if (item == icon.value) 1.dp else 0.dp, ambientColor = colors.secondary)
              .size(70.dp)
              .clickable { changeIcon(item) }
              .padding(10.dp)
          )

          if (index + 1 != AppIcon.values().size) {
            Spacer(Modifier.padding(horizontal = 4.dp))
          }
        }
      }
    }

    SectionView(stringResource(R.string.settings_section_title_theme)) {
      val darkTheme = isSystemInDarkTheme()
      val currentTheme by CurrentTheme.collectAsState()
      LazyColumn(
        Modifier.padding(horizontal = 8.dp)
      ) {
        items(allThemes.size) { index ->
          val item = allThemes[index]
          val onClick = {
            // Edit theme when it's currently selected already, and it's not a default one
            if (ThemeManager.currentTheme(darkTheme).name == item.first.name && index > DefaultThemes.values().lastIndex) {
              editTheme(item.first)
            } else {
              ThemeManager.applyTheme(item.first.name, darkTheme)
            }
          }
          SectionItemViewSpaceBetween(onClick, padding = PaddingValues()) {
            Text(item.second)
            if (currentTheme.name == item.first.name) {
              Icon(Icons.Outlined.Check, item.second, tint = HighOrLowlight)
            } else if (item.first.name !in DefaultThemes.values().map { it.name }) {
              Icon(
                Icons.Outlined.Delete, item.second,
                Modifier.clickable { removeTheme(item.first.name) },
                tint = colors.error
              )
            }
          }
          Spacer(Modifier.padding(horizontal = 4.dp))
        }
        item {
          SectionItemViewSpaceBetween(createTheme, padding = PaddingValues()) {
            Text(generalGetString(R.string.create_theme), color = colors.primary)
            Icon(Icons.Outlined.Add, generalGetString(R.string.create_theme), tint = colors.primary)
          }
        }
      }
    }
  }
}

fun showThemeEditor(
  oldTheme: CustomTheme?,
  darkTheme: Boolean,
  allThemes: MutableState<List<Pair<CustomTheme, String>>>,
  showCustomModal: (@Composable (close: () -> Unit) -> Unit) -> Unit,
) {
  val oldThemeName = oldTheme?.name
  showCustomModal { close ->
    val initialName = oldTheme?.name ?: ThemeManager.generateNameFromTheme()
    var themeName by remember { mutableStateOf(initialName) }
    var showAlert by remember { mutableStateOf(true) }
    if (showAlert) {
      AlertManager.shared.showTextAlertDialog(
        generalGetString(R.string.theme_name),
        initialName,
        {},
        generalGetString(R.string.ok),
        null,
        {
          themeName = it;
          showAlert = false

          // Do not allow empty name
          if (it.isBlank()) close()
        },
        {
          showAlert = false
          // Do not allow empty name
          if (themeName.isBlank()) close()
        }
      )
    }

    Column(
      Modifier
        .fillMaxWidth()
    ) {
      var newTheme by remember {
        mutableStateOf(
          oldTheme ?: CustomTheme(
            name = themeName,
            colors = if (!darkTheme) CustomThemeColors.fromColors(LightColorPalette) else CustomThemeColors.fromColors(DarkColorPalette)
          )
        )
      }
      val colorNames = ThemeColorComponent.values()
      var selectedColorName by remember { mutableStateOf(colorNames.first()) }
      var currentColor by remember { mutableStateOf(selectedColorName.colorFromComponent(newTheme)) }

      ColorPicker(Color(selectedColorName.colorFromComponent(newTheme))) {
        currentColor = it
      }

      SectionSpacer()

      Button(
        onClick = {
          newTheme = newTheme.copy(name = themeName)
          newTheme = selectedColorName.updateColorFromComponent(currentColor, newTheme)
          if (oldTheme == null) {
            ThemeManager.addCustomTheme(newTheme, true)
          } else {
            ThemeManager.editCustomTheme(oldThemeName!!, newTheme, true)
          }
          allThemes.value = ThemeManager.allThemes(darkTheme)
          close()
        },
        Modifier.align(Alignment.CenterHorizontally),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(currentColor))
      ) {
        Text(generalGetString(R.string.save_theme))
      }

      SectionSpacer()

      SectionView(stringResource(R.string.settings_section_title_theme_colors)) {
        LazyColumn(
          Modifier.padding(horizontal = 8.dp)
        ) {
          items(colorNames.size) { index ->
            val item = colorNames[index]
            val onClick = {
              newTheme = selectedColorName.updateColorFromComponent(currentColor, newTheme)
              selectedColorName = item
              currentColor = selectedColorName.colorFromComponent(newTheme)
            }
            SectionItemViewSpaceBetween(onClick, padding = PaddingValues()) {
              Text(item.name.lowercase())
              if (selectedColorName == item) {
                Icon(Icons.Outlined.Check, item.name.lowercase(), tint = HighOrLowlight)
              }
            }
            Spacer(Modifier.padding(horizontal = 4.dp))
          }
        }
      }
    }
  }
}

@Composable
fun ColorPicker(initialColor: Color, onColorChanged: (Int) -> Unit) {
  ClassicColorPicker(
    color = initialColor,
    modifier = Modifier
      .fillMaxWidth()
      .height(300.dp),
    onColorChanged = { color: HsvColor ->
      onColorChanged(color.toColorInt())
    }
  )
}

private fun findEnabledIcon(): AppIcon = AppIcon.values().first { icon ->
  SimplexApp.context.packageManager.getComponentEnabledSetting(
    ComponentName(BuildConfig.APPLICATION_ID, "chat.simplex.app.MainActivity_${icon.name.lowercase()}")
  ).let { it == COMPONENT_ENABLED_STATE_DEFAULT || it == COMPONENT_ENABLED_STATE_ENABLED }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppearanceSettings() {
  SimpleXTheme {
    AppearanceLayout(
      icon = remember { mutableStateOf(AppIcon.DARK_BLUE) },
      allThemes = listOf(),
      changeIcon = {},
      createTheme = {},
      editTheme = {},
      removeTheme = {},
    )
  }
}
