package com.anisanurjanah.dicodingstoryapp.view.addstory

import androidx.lifecycle.ViewModel
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository): ViewModel() {

    fun uploadNewStory(getFile: File?, description: String) = repository.uploadNewStory(getFile, description)

}