package com.anisanurjanah.dicodingstoryapp.view.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityProfileBinding
import com.anisanurjanah.dicodingstoryapp.view.history.HistoryActivity
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.anisanurjanah.dicodingstoryapp.view.maps.MapsActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_account
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
                R.id.navigation_history -> {
                    moveToHistory()
                    true
                }
                else -> false
            }
        }
    }

    private fun moveToHome() {
        startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
    }

    private fun moveToMaps() {
        startActivity(Intent(this@ProfileActivity, MapsActivity::class.java))
    }

    private fun moveToHistory() {
        startActivity(Intent(this@ProfileActivity, HistoryActivity::class.java))
    }

}