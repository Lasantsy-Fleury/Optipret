package com.optipret.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"
private const val DEFAULT_API_URL = "https://optipret-backend.onrender.com"
private const val DEFAULT_CURRENCY = "ARIARY"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager(private val context: Context) {
  private object Keys {
    val apiUrl = stringPreferencesKey("api_url")
    val apiUrlMigrated = booleanPreferencesKey("api_url_migrated")
    val currency = stringPreferencesKey("currency")
    val offlineMode = booleanPreferencesKey("offline_mode")
  }

  val apiUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
    prefs[Keys.apiUrl] ?: DEFAULT_API_URL
  }

  val currencyFlow: Flow<String> = context.dataStore.data.map { prefs ->
    prefs[Keys.currency] ?: DEFAULT_CURRENCY
  }

  val offlineModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[Keys.offlineMode] ?: false
  }

  suspend fun setApiUrl(value: String) {
    context.dataStore.edit { prefs ->
      prefs[Keys.apiUrl] = value
    }
  }

  suspend fun ensureDefaultApiUrlIfNeeded() {
    val prefs = context.dataStore.data.first()
    val migrated = prefs[Keys.apiUrlMigrated] ?: false
    val current = prefs[Keys.apiUrl]

    if (!migrated && current != DEFAULT_API_URL) {
      context.dataStore.edit { edit ->
        edit[Keys.apiUrl] = DEFAULT_API_URL
        edit[Keys.apiUrlMigrated] = true
      }
    }
  }

  suspend fun setCurrency(value: String) {
    context.dataStore.edit { prefs ->
      prefs[Keys.currency] = value
    }
  }

  suspend fun setOfflineMode(value: Boolean) {
    context.dataStore.edit { prefs ->
      prefs[Keys.offlineMode] = value
    }
  }
}
