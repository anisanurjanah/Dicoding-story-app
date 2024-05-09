package com.anisanurjanah.dicodingstoryapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: StoryRepository,
    private val pref: UserPreference
) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveLoginState(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
            pref.login()
        }
    }
}