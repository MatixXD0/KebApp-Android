package pl.kul.kebapp.locale

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf
import java.util.Locale

val LocalAppLocale = compositionLocalOf { Locale.getDefault() }

fun Context.withLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}