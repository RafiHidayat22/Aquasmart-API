package com.example.aquasmart.ui.Splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aquasmart.MainActivity
import com.example.aquasmart.databinding.ActivitySplashBinding
import com.example.aquasmart.ui.auth.Login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        playAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPref.getString("TOKEN_KEY", null)

            val nextActivity = if (token != null) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000)
    }

    private fun playAnimations() {
        binding.ivTop.animate()
            .translationX(-500f)
            .alpha(0f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.ivBottom.animate()
            .translationX(500f)
            .alpha(0f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.logo.alpha = 0f
        binding.logo.animate()
            .alpha(1f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.textLogo.alpha = 0f
        binding.textLogo.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .start()

        binding.tagline.alpha = 0f
        binding.tagline.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(800)
            .start()
    }

}