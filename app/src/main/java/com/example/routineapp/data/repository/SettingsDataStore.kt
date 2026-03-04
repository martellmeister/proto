package com.example.routineapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingDoneKey = booleanPreferencesKey("onboarding_done")

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs: Preferences ->
        prefs[onboardingDoneKey] ?: false
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { prefs -> prefs[onboardingDoneKey] = done }
    }
}
