package com.anisanurjanah.dicodingstoryapp.view.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anisanurjanah.dicodingstoryapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityMapsBinding
import com.anisanurjanah.dicodingstoryapp.view.history.HistoryActivity
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.anisanurjanah.dicodingstoryapp.view.profile.ProfileActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    private fun setupAction() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_maps
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    moveToHome()
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

    private fun moveToHome() {
        startActivity(Intent(this@MapsActivity, MainActivity::class.java))
    }

    private fun moveToHistory() {
        startActivity(Intent(this@MapsActivity, HistoryActivity::class.java))
    }

    private fun moveToProfile() {
        startActivity(Intent(this@MapsActivity, ProfileActivity::class.java))
    }
}