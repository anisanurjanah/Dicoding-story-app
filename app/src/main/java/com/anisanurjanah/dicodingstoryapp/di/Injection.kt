package com.anisanurjanah.dicodingstoryapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.remote.local.StoryDatabase
import com.anisanurjanah.dicodingstoryapp.data.remote.retrofit.ApiConfig
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token.toString())

        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, storyDatabase)
    }
}