package pl.kul.kebapp.locale

import android.content.Context
import java.util.Locale
import androidx.core.content.edit

object LocaleManager {
    private const val PREFS_NAME = "settings"
    private const val LANGUAGE_KEY = "lang"

    fun saveLanguage(context: Context, locale: Locale) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(LANGUAGE_KEY, locale.language) }
    }

    fun getSavedLocale(context: Context? = null): Locale {
        val langCode = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(LANGUAGE_KEY, Locale.getDefault().language) ?: "en"
        return Locale(langCode)
    }
}