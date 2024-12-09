package com.example.aquasmart.ui.auth.Login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aquasmart.MainActivity
import com.example.aquasmart.Utils.FormValidator
import com.example.aquasmart.databinding.ActivityLoginBinding
import com.example.aquasmart.ui.auth.Register.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val formValidator = FormValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        supportActionBar?.hide()
        binding.title.alpha = 0f
        binding.subtitle.alpha = 0f

        formValidator.setupTextWatchers(
            emailInputLayout = binding.emailInputLayout,
            passwordInputLayout = binding.passwordInputLayout
        )


        setupView()
        checkLoginStatus()

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && binding.emailInputLayout.error == null && binding.passwordInputLayout.error == null) {
                updateLoadingState(true)
                loginUser(email, password)
            } else {
                if (email.isEmpty()) {
                    binding.emailInputLayout.error = "Email tidak boleh kosong"
                }
                if (password.isEmpty()) {
                    binding.passwordInputLayout.error = "Password tidak boleh kosong"
                }
                Toast.makeText(this, "Silakan isi semua kolom dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signUpLink.setOnClickListener {
            Intent(this, SignUpActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }

    private fun setupView() {
        animationLogin()
    }

    private fun animationLogin() {
        val logoFadeIn = ObjectAnimator.ofFloat(binding.logo, "alpha", 0f, 1f).apply {
            duration = 1000
        }
        val logoTranslateY = ObjectAnimator.ofFloat(binding.logo, "translationY", -100f, 0f).apply {
            duration = 1000
        }
        val logoAnimatorSet = AnimatorSet().apply {
            playTogether(logoFadeIn, logoTranslateY)
        }
        logoAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animateTitleAsTyping()
            }
        })
        logoAnimatorSet.start()
    }

    private fun animateTitleAsTyping() {
        binding.title.alpha = 1f
        val titleText = binding.title.text.toString()
        binding.title.text = ""

        val animator = ValueAnimator.ofInt(0, titleText.length)
        animator.duration = 1500
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val index = valueAnimator.animatedValue as Int
            binding.title.text = titleText.substring(0, index)
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animateSubtitleAsTyping()
            }
        })
        animator.start()
    }

    private fun animateSubtitleAsTyping() {
        binding.subtitle.alpha = 1f
        val subtitleText = binding.subtitle.text.toString()
        binding.subtitle.text = ""

        val subtitleAnimator = ValueAnimator.ofInt(0, subtitleText.length)
        subtitleAnimator.duration = 1500
        subtitleAnimator.interpolator = LinearInterpolator()
        subtitleAnimator.addUpdateListener { valueAnimator ->
            val index = valueAnimator.animatedValue as Int
            binding.subtitle.text = subtitleText.substring(0, index)
        }
        subtitleAnimator.start()
    }


    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (token != null) {
            navigateToMainActivity()
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.apply {
            progressOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) disableInput() else enableInput()
        }
    }

    private fun enableInput() {
        binding.apply {
            emailInput.isEnabled = true
            passwordInput.isEnabled = true
        }
    }

    private fun disableInput() {
        binding.apply {
            emailInput.isEnabled = false
            passwordInput.isEnabled = false
        }
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.login(
            this,
            email,
            password,
            onSuccess = { token ->
                Toast.makeText(this, "Login berhasil! Token: $token", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            },
            onError = { message ->
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

}