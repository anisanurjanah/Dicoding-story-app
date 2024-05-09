package com.anisanurjanah.dicodingstoryapp.data.repository

import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.remote.retrofit.ApiService

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    companion object {
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}