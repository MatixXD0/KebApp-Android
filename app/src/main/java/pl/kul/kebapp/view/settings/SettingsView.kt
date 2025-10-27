package pl.kul.kebapp.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.kul.kebapp.R
import pl.kul.kebapp.locale.LocalAppLocale
import pl.kul.kebapp.ui.theme.AppTheme
import pl.kul.kebapp.ui.theme.ThemePreferences
import pl.kul.kebapp.view.settings.components.LanguageDropdown
import pl.kul.kebapp.view.settings.components.ThemeDropdown
import java.util.Locale

@Composable
fun SettingsView(
    onLogout: () -> Unit,
    onLocaleChange: (Locale) -> Unit
) {
    val context = LocalContext.current

    val themePreferences = remember { ThemePreferences(context) }
    var currentTheme by remember { mutableStateOf(AppTheme.SYSTEM) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        currentTheme = themePreferences.getTheme()
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            LanguageDropdown(
                currentLocale = LocalAppLocale.current,
                onLocaleChange = onLocaleChange
            )

            ThemeDropdown(
                currentTheme = currentTheme,
                onThemeChange = { newTheme ->
                    currentTheme = newTheme
                    coroutineScope.launch {
                        themePreferences.saveTheme(newTheme)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.log_out), color = Color.White)
            }
        }
    }
}
