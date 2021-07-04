package com.example.puyo_base_simulator.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingRepository (private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val showDoubleNext = booleanPreferencesKey("show_double_next")
    }

    private val Preferences.showDoubleNext
        get() = this[Keys.showDoubleNext] ?: true

    val showDoubleNextFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.showDoubleNext] ?: true
    }

    suspend fun updateShowDoubleNext(show: Boolean) {
        dataStore.edit { it[Keys.showDoubleNext] = show }
    }
}