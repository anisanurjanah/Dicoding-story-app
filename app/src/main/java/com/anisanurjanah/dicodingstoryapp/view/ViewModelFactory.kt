package com.anisanurjanah.dicodingstoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import com.anisanurjanah.dicodingstoryapp.di.Injection
import com.anisanurjanah.dicodingstoryapp.view.addstory.AddStoryViewModel
import com.anisanurjanah.dicodingstoryapp.view.login.LoginViewModel
import com.anisanurjanah.dicodingstoryapp.view.main.MainViewModel
import com.anisanurjanah.dicodingstoryapp.view.maps.MapsViewModel
import com.anisanurjanah.dicodingstoryapp.view.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val repository: StoryRepository,
    private val pref: UserPreference
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, pref) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository, pref) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, pref: UserPreference): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), pref)
            }.also { instance = it }
    }
}