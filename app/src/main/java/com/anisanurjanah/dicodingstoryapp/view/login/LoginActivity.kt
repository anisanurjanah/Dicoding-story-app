package com.anisanurjanah.dicodingstoryapp.view.login

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linoleumBlue = ContextCompat.getColor(this, R.color.linoleum_blue)

        val spannable = SpannableString(getString(R.string.login_title) + " " + getString(R.string.dicoding_story))
        spannable.setSpan(
            ForegroundColorSpan(linoleumBlue),
            getString(R.string.login_title).length + 1,
            getString(R.string.login_title).length + 1 + getString(R.string.dicoding_story).length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.loginTitle.text = spannable
    }
}