package com.anisanurjanah.dicodingstoryapp.view.register

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linoleumBlue = ContextCompat.getColor(this, R.color.linoleum_blue)

        val signupString = getString(R.string.signup_title, getString(R.string.dicoding_story))
        val spannable = SpannableString(signupString)

        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            signupString.indexOf(getString(R.string.dicoding_story)),
            signupString.indexOf(getString(R.string.dicoding_story)) + getString(R.string.dicoding_story).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.signupTitle.text = spannable
    }
}