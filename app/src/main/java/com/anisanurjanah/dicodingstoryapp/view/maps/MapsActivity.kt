package com.anisanurjanah.dicodingstoryapp.view.maps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityMapsBinding
import com.anisanurjanah.dicodingstoryapp.view.ViewModelFactory
import com.anisanurjanah.dicodingstoryapp.view.history.HistoryActivity
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.anisanurjanah.dicodingstoryapp.view.profile.ProfileActivity
import com.anisanurjanah.dicodingstoryapp.view.register.RegisterActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    private lateinit var binding: ActivityMapsBinding

    private lateinit var mapsViewModel: MapsViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(RegisterActivity.SESSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        mapsViewModel = obtainViewModel(this@MapsActivity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getUserLocation()
        setupStoryMarkers()
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

    private fun setMapStyle() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val style = when (nightMode) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.dark_map_style
            Configuration.UI_MODE_NIGHT_NO -> R.raw.light_map_style
            else -> R.raw.light_map_style
        }

        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getUserLocation()
            }
        }
    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupStoryMarkers() {
        mapsViewModel.getStoriesWithLocation().observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)

                        val response = it.data
                        response.forEach { data ->
                            if (data.lat != null && data.lon != null) {
                                val latLng = LatLng(data.lat, data.lon)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(data.name)
                                        .snippet(data.description)
                                )
                                boundsBuilder.include(latLng)
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
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

    private fun moveToHome() {
        startActivity(Intent(this@MapsActivity, MainActivity::class.java))
    }

    private fun moveToHistory() {
        startActivity(Intent(this@MapsActivity, HistoryActivity::class.java))
    }

    private fun moveToProfile() {
        startActivity(Intent(this@MapsActivity, ProfileActivity::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): MapsViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[MapsViewModel::class.java]
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}