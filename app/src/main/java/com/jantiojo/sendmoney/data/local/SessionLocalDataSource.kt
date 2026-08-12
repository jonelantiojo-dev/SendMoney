package com.jantiojo.sendmoney.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.sessionDataStore by preferencesDataStore(
    name = "session_preferences"
)

class SessionLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
    }

    val isLoggedIn: Flow<Boolean> =
        context.sessionDataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun saveSession(
        username: String
    ) {
        context.sessionDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USERNAME] = username
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
