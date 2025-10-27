package pl.kul.kebapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Composable
fun ThemeWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val selectedTheme by themePreferences.observeTheme().collectAsState(initial = AppTheme.SYSTEM)

    val isDarkTheme = when (selectedTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }

    KebAppTheme(darkTheme = isDarkTheme) {
        content()
    }
}
