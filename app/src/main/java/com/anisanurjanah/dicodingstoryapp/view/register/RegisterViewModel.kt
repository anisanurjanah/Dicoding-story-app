package com.anisanurjanah.dicodingstoryapp.view.register

import androidx.lifecycle.ViewModel
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {

    fun register(name: String, email: String, password: String) = repository.register(name, email, password)

}