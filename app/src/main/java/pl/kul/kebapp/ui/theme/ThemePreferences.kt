package pl.kul.kebapp.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme")
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun getTheme(): AppTheme {
        val themeName = context.dataStore.data
            .map { it[THEME_KEY] ?: AppTheme.SYSTEM.name }
            .first()

        return AppTheme.valueOf(themeName)
    }

    fun observeTheme() = context.dataStore.data
        .map { preferences ->
            AppTheme.valueOf(preferences[THEME_KEY] ?: AppTheme.SYSTEM.name)
        }
}