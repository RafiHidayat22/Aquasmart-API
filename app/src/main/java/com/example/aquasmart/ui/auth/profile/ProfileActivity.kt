package com.example.aquasmart.ui.auth.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        // Mendapatkan token autentikasi
        val token = getAuthToken()

        // Membuat ViewModel dan memonitor data profil
        val factory = ProfileViewModelFactory(application, ApiConfig.apiService)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        // Memantau data profil yang diterima
        profileViewModel.profileData.observe(this, { profileResponse ->
            profileResponse?.let {
                Log.d("ProfileData", "Received profile data: ${it.data.profilePicture}")
                binding.nameProfile.setText(it.data.name)
                binding.emailProfile.setText(it.data.email)
                binding.phoneProfile.setText(it.data.phoneNumber)
                binding.birthdateProfile.setText(it.data.dateBirth)

                // Menampilkan gambar profil menggunakan Glide
                val imageUrl = it.data.profilePicture // Pastikan URL yang diterima valid
                Glide.with(this)
                    .load(imageUrl) // Memuat URL gambar
                    .placeholder(R.drawable.placeholder_image) // Gambar placeholder saat loading
                    .error(R.drawable.error_image) // Gambar error jika gagal
                    .into(binding.imageView) // Menampilkan gambar di ImageView
            }
        })

        // Memantau pesan error
        profileViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        // Memantau status update gambar
        profileViewModel.updateStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        // Mengambil data profil
        profileViewModel.getProfile("Bearer $token")

        // Menangani aksi button
        binding.btnPickImage.setOnClickListener {
            openImagePicker()
        }
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // Membuka pemilih gambar
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    // Launcher untuk menangani hasil pemilihan gambar
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                selectedImageUri?.let {
                    Log.d("ImagePicker", "Selected URI: $it")
                    binding.imageView.setImageURI(it) // Menampilkan gambar yang dipilih
                    val file = uriToFile(it, applicationContext) // Mengonversi URI ke File
                    profileViewModel.updateProfilePicture("Bearer ${getAuthToken()}", file) // Mengupdate gambar profil
                }
            }
        }

    // Mengambil token autentikasi dari SharedPreferences
    private fun getAuthToken(): String {
        val sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", "") ?: ""
    }

    // Menampilkan dialog konfirmasi logout
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

    // Logout dan menghapus token autentikasi dari SharedPreferences
    private fun logout() {
        val sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("auth_token")
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent) // Mengarahkan ke halaman login
        finish() // Menutup aktivitas ini
    }
}
