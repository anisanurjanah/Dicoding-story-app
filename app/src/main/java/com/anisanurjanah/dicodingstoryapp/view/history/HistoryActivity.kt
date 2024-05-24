package com.anisanurjanah.dicodingstoryapp.view.history

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityHistoryBinding
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.anisanurjanah.dicodingstoryapp.view.maps.MapsActivity
import com.anisanurjanah.dicodingstoryapp.view.profile.ProfileActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_history
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    moveToHome()
                    true
                }
                R.id.navigation_maps -> {
                    moveToMaps()
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

    private fun moveToHome() {
        startActivity(Intent(this@HistoryActivity, MainActivity::class.java))
        finish()
    }

    private fun moveToMaps() {
        startActivity(Intent(this@HistoryActivity, MapsActivity::class.java))
        finish()
    }

    private fun moveToProfile() {
        startActivity(Intent(this@HistoryActivity, ProfileActivity::class.java))
        finish()
    }
}