package com.anisanurjanah.dicodingstoryapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityMainBinding
import com.anisanurjanah.dicodingstoryapp.view.ViewModelFactory
import com.anisanurjanah.dicodingstoryapp.view.addstory.AddStoryActivity
import com.anisanurjanah.dicodingstoryapp.view.detailstory.DetailStoryActivity
import com.anisanurjanah.dicodingstoryapp.view.login.LoginActivity
import com.anisanurjanah.dicodingstoryapp.view.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = obtainViewModel(this@MainActivity)

        setupBar()
        setupStories()
        setupAction()
    }

    private fun setupBar() {
        with(binding) {
            topAppBar.inflateMenu(R.menu.option_menu)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        moveToSetting()
                        true
                    }
                    R.id.menu_logout -> {
                        moveToLogin()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabButton.setOnClickListener { moveToAddNewStory() }
    }

    private fun setupStories() {
        val storyAdapter = StoryAdapter()

        binding.rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvStories.adapter = storyAdapter

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryItem?) {
                moveToDetailStory(data)
            }
        })

        mainViewModel.stories.observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)

                        val storyItem = it.data

                        if (storyItem.isEmpty()) {
                            binding.rvStories.visibility = View.GONE
                            binding.storyNotAvailable.visibility = View.VISIBLE
                        } else {
                            binding.rvStories.visibility = View.VISIBLE
                            binding.storyNotAvailable.visibility = View.GONE

                            storyAdapter.submitList(storyItem)
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        }
    }

    private fun moveToDetailStory(item: StoryItem?) {
        val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.EXTRA_RESULT, item)
        startActivity(intent)
    }

    private fun moveToAddNewStory() {
        startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
    }

    private fun moveToSetting() {
        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
    }

    private fun moveToLogin() {
        mainViewModel.logout()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
//        const val SETTING = "settings"
    }
}