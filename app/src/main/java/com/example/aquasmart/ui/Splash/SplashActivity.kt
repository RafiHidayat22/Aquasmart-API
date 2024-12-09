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
import com.example.aquasmart.R
import com.example.aquasmart.databinding.ActivitySplashBinding
import com.example.aquasmart.ui.auth.Login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menghubungkan binding dengan layout
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge() // Aktifkan edge-to-edge untuk tampilan fullscreen

        // Memutar animasi
        playAnimations()

        // Simulasi penundaan untuk splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPref.getString("TOKEN_KEY", null)

            val nextActivity = MainActivity::class.java

            // Pindah ke aktivitas berikutnya (MainActivity atau LoginActivity)
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000) // Delay 2 detik untuk splash screen
    }

    // Fungsi untuk memutar animasi pada elemen-elemen UI
    private fun playAnimations() {
        // Animasi untuk elemen top (misalnya gambar)
        binding.ivTop.animate()
            .translationX(-500f)
            .alpha(0f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animasi untuk elemen bottom
        binding.ivBottom.animate()
            .translationX(500f)
            .alpha(0f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animasi untuk logo utama
        binding.logo.alpha = 0f
        binding.logo.animate()
            .alpha(1f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animasi untuk teks logo
        binding.textLogo.alpha = 0f
        binding.textLogo.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(500)
            .start()

        // Animasi untuk tagline
        binding.tagline.alpha = 0f
        binding.tagline.animate()
            .alpha(1f)
            .setDuration(1500)
            .setStartDelay(800)
            .start()
    }

}