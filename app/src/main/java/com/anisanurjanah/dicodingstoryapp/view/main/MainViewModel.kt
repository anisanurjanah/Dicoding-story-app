package com.anisanurjanah.dicodingstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StoryRepository,
    private val pref: UserPreference
) : ViewModel() {

    val stories: LiveData<Result<List<StoryItem>>> = repository.getAllStories()

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}