package com.anisanurjanah.dicodingstoryapp.view.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityAddStoryBinding
import com.anisanurjanah.dicodingstoryapp.utils.uriToFile
import com.anisanurjanah.dicodingstoryapp.view.ViewModelFactory
import com.anisanurjanah.dicodingstoryapp.view.camera.CameraActivity
import com.anisanurjanah.dicodingstoryapp.view.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var storyViewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double? = null
    private var currentLon: Double? = null

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        storyViewModel = obtainViewModel(this@AddStoryActivity)

        showLoading(false)
        setupToolbar()
        setupAction()
        setupAccessibility()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupToolbar() {
        with(binding) {
            topAppBar.title = getString(R.string.add_new_story)

            topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
            topAppBar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCameraX() }
        binding.uploadButton.setOnClickListener { uploadStory() }
        binding.shareLocation.setOnCheckedChangeListener { _, isChecked -> shareLocation(isChecked) }
    }

    private fun setupAccessibility() {
        binding.apply {
            topAppBar.contentDescription = getString(R.string.navigation_and_actions)
            previewImageView.contentDescription = getString(R.string.preview_of_selected_image)
            galleryButton.contentDescription = getString(R.string.open_gallery_to_select_image)
            cameraButton.contentDescription = getString(R.string.take_photo_with_camera)
            descriptionEditText.contentDescription = getString(R.string.enter_description_for_the_image)
            uploadButton.contentDescription = getString(R.string.upload_the_selected_image)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", getString(R.string.no_media_selected))
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                showToast(getString(R.string.permission_request_granted))
            } else {
                showToast(getString(R.string.permission_request_denied))
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getUserLocation(onLocationReceived: (Double?, Double?) -> Unit) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    showToast(getString(R.string.location_not_found))
                    onLocationReceived(null, null)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadStory() {
        if (currentImageUri == null) {
            showToast(getString(R.string.please_select_an_image_first))
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            val descriptionBody = binding.descriptionEditText.text.toString()

            if (binding.shareLocation.isChecked) {
                if (currentLat == null || currentLon == null) {
                    showToast(getString(R.string.enable_location_first))
                    getUserLocation { lat, lon ->
                        currentLat = lat
                        currentLon = lon
                        if (lat != null && lon != null) {
                            uploadStoryWithLocation(imageFile, descriptionBody, lat, lon)
                        } else {
                            showToast(getString(R.string.location_not_found))
                        }
                    }
                } else {
                    uploadStoryWithLocation(imageFile, descriptionBody, currentLat, currentLon)
                }
            } else {
                uploadStoryWithLocation(imageFile, descriptionBody, null, null)
            }
        }
    }

    private fun uploadStoryWithLocation(imageUri: File, description: String, lat: Double?, lon: Double?) {
        storyViewModel.uploadNewStory(imageUri, description, lat, lon).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val response = it.data
                        showToast(response.message.toString())

                        moveToMain()
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(it.error)
                    }
                }
            }
        }
    }

    private fun shareLocation(isChecked: Boolean) {
        if (isChecked) {
            getUserLocation { lat, lon ->
                currentLat = lat
                currentLon = lon
            }
        } else {
            currentLat = null
            currentLon = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): AddStoryViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[AddStoryViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}