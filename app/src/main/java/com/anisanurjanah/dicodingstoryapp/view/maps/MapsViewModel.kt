package com.anisanurjanah.dicodingstoryapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository

class MapsViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    fun getStoriesWithLocation(): LiveData<Result<List<StoryItem>>> {
        return repository.getStoriesWithLocation()
    }

}