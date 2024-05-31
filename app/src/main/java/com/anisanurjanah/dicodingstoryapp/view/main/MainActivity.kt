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
import com.anisanurjanah.dicodingstoryapp.view.detailstory.DetailStoryDialogFragment
import com.anisanurjanah.dicodingstoryapp.view.history.HistoryActivity
import com.anisanurjanah.dicodingstoryapp.view.login.LoginActivity
import com.anisanurjanah.dicodingstoryapp.view.maps.MapsActivity
import com.anisanurjanah.dicodingstoryapp.view.profile.ProfileActivity
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

        setupToolbar()
        setupAction()
        setupAccessibility()
        setupStory()
    }

    private fun setupToolbar() {
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

        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        binding.bottomNavigation.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_maps -> {
                    moveToMaps()
                    true
                }
                R.id.navigation_history -> {
                    moveToHistory()
                    true
                }
                R.id.navigation_account -> {
                    moveToProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            dicodingImage.contentDescription = getString(R.string.dicoding_s_logo)
            titleTextView.contentDescription = getString(R.string.title_of_dicoding_logo)
            rvStories.contentDescription = getString(R.string.list_of_stories)
            fabButton.contentDescription = getString(R.string.upload_new_story)
        }
    }

    private fun setupStory() {
        val storyAdapter = StoryAdapter()

        binding.rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rvStories.adapter = storyAdapter
        storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(items: StoryItem?) {
                items?.let {
                    val dialogFragment = DetailStoryDialogFragment.newInstance(it)
                    dialogFragment.show(supportFragmentManager, "DetailStoryDialogFragment")
                }
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

                        val response = it.data
                        storyAdapter.submitData(lifecycle, response)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        }
    }

    private fun moveToAddNewStory() {
        startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
    }

    private fun moveToMaps() {
        startActivity(Intent(this@MainActivity, MapsActivity::class.java))
    }

    private fun moveToHistory() {
        startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
    }

    private fun moveToProfile() {
        startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
    }

    private fun moveToSetting() {
        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
    }

    private fun moveToLogin() {
        mainViewModel.logout()
        showToast(getString(R.string.successfully_logged_out))
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
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

    override fun onResume() {
        super.onResume()
        mainViewModel.stories
    }

    companion object {
        const val SESSION = "session"
    }
}