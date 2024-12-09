package com.example.aquasmart

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aquasmart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        try {
            // Temukan NavController menggunakan ID dari NavHostFragment
            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            // Tentukan AppBarConfiguration untuk destinasi top-level
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_water,
                    R.id.navigation_guide,
                    R.id.navigation_predict,
                    R.id.navigation_report,
                    R.id.navigation_weather
                )
            )

            // Setup ActionBar untuk mendukung NavController
            setupActionBarWithNavController(navController, appBarConfiguration)

            // Menghubungkan BottomNavigationView dengan NavController
            navView.setupWithNavController(navController)

        } catch (e: Exception) {
            Log.e("MainActivity", "Error finding NavController: ${e.message}")
        }
    }
}