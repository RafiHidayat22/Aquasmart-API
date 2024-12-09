package com.example.aquasmart.ui.auth.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aquasmart.API.ApiConfig
import com.example.aquasmart.R
import com.example.aquasmart.databinding.ActivityProfileBinding
import com.example.aquasmart.databinding.CustomLogoutDialogBinding
import com.example.aquasmart.ui.auth.Login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()

        val token = getAuthToken()
        val factory = ProfileViewModelFactory(application, ApiConfig.apiService)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)



        // Observasi LiveData dari ViewModel untuk data profil
        profileViewModel.profileData.observe(this, { profileResponse ->
            profileResponse?.let {
                // Memperbarui UI dengan data profil
                binding.nameProfile.setText(it.data.name)
                binding.emailProfile.setText(it.data.email)
                binding.phoneProfile.setText(it.data.phoneNumber)
                binding.birthdateProfile.setText(it.data.dateBirth)

                // Memuat gambar profil menggunakan Glide
                Glide.with(this)
                    .load(it.data.profilePicture)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.imageView)
            }
        })

        // Menampilkan pesan kesalahan
        profileViewModel.errorMessage.observe(this, { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        // Menampilkan status pembaruan foto profil
        profileViewModel.updateStatus.observe(this, { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        })

        // Memuat data profil saat aktivitas dimulai
        profileViewModel.getProfile("Bearer $token")

        // Menangani pemilihan gambar untuk update foto profil
        binding.btnPickImage.setOnClickListener {
            openImagePicker()
        }
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // Fungsi untuk membuka image picker
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    // Fungsi untuk menangani hasil pemilihan gambar
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                selectedImageUri?.let {
                    // Menampilkan gambar yang dipilih di ImageView
                    binding.imageView.setImageURI(it)

                    // Mengonversi Uri ke File dan mengirimnya ke ViewModel untuk diupload
                    val file = uriToFile(it, applicationContext)
                    profileViewModel.updateProfilePicture("Bearer ${getAuthToken()}", file)
                }
            }
        }

    // Fungsi untuk mendapatkan token dari SharedPreferences
    private fun getAuthToken(): String {
        val sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", "") ?: ""
    }
    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_logout_dialog, null)
        val binding = CustomLogoutDialogBinding.bind(dialogView)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = builder.create()
        binding.btnYes.setOnClickListener {
            logout()
            alertDialog.dismiss()
        }
        binding.btnNo.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun logout() {
        // Menghapus token dari SharedPreferences
        val sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("auth_token") // Menghapus token login
            apply()
        }

        // Mengarahkan kembali ke LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Menutup ProfileActivity agar tidak bisa kembali
    }
}