package pl.kul.kebapp.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun LocaleProvider(
    content: @Composable (onLocaleChange: (Locale) -> Unit) -> Unit
) {
    val baseContext = LocalContext.current
    var locale by remember { mutableStateOf(LocaleManager.getSavedLocale(baseContext)) }

    CompositionLocalProvider(LocalAppLocale provides locale) {
        val localizedContext = baseContext.withLocale(locale)
        CompositionLocalProvider(LocalContext provides localizedContext) {
            content { newLocale ->
                LocaleManager.saveLanguage(baseContext, newLocale)
                locale = newLocale
            }
        }
    }
}
