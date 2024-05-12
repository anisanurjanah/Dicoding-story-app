package com.anisanurjanah.dicodingstoryapp.view.login

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityLoginBinding
import com.anisanurjanah.dicodingstoryapp.view.ViewModelFactory
import com.anisanurjanah.dicodingstoryapp.view.main.MainActivity
import com.anisanurjanah.dicodingstoryapp.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SESSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTitle()
        setupButton()

        showLoading(false)
        setupAction()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener { setupLogin() }
    }

    private fun setupTitle() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.linoleum_blue)

        val loginTitle = getString(R.string.login_title)
        val dicodingStory = getString(R.string.dicoding_story)

        val spannable = SpannableString("$loginTitle $dicodingStory")
        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            loginTitle.length + 1,
            loginTitle.length + 1 + dicodingStory.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginTitle.text = spannable
    }

    private fun setupButton() {
        val linoleumBlue = ContextCompat.getColor(this, R.color.linoleum_blue)

        val spannable = SpannableString(getString(R.string.login_to_signup, getString(R.string.signup_now)))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(
            boldSpan,
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            clickableSpan,
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            spannable.indexOf(getString(R.string.signup_now)),
            spannable.indexOf(getString(R.string.signup_now)) + getString(R.string.signup_now).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.registerButton.text = spannable
        binding.registerButton.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupLogin() {
        val loginViewModel = obtainViewModel(this@LoginActivity)

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        when {
            email.isEmpty() -> {
                binding.emailEditText.error = getString(R.string.empty_email)
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = getString(R.string.empty_password)
            }
            else -> {
                loginViewModel.login(email, password).observe(this@LoginActivity) {
                    if (it != null) {
                        when (it) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)

                                val loginResult = it.data
                                loginViewModel.saveLoginState(loginResult.token.toString())

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
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
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(
            activity.application,
            UserPreference.getInstance(dataStore)
        )
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }

    companion object {
        const val SESSION = "session"
    }
}