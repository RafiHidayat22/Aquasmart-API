package com.example.aquasmart.ui.auth.Register

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aquasmart.API.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData untuk status registrasi
    val registrationStatus = MutableLiveData<String>()
    // LiveData untuk status loading
    val isLoading = MutableLiveData<Boolean>()

    // Fungsi untuk melakukan registrasi
    @RequiresApi(Build.VERSION_CODES.S)
    fun registerUser(
        fullName: String,
        email: String,
        birthDate: String,
        phoneNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            // Menandakan bahwa proses registrasi dimulai (loading)
            isLoading.value = true

            try {
                // Melakukan request ke API
                val response = withContext(Dispatchers.IO) {
                    // Pastikan apiService sesuai dengan parameter yang diperlukan
                    ApiConfig.apiService.register(fullName, email, birthDate, phoneNumber, password)
                }

                // Cek status response dari server
                if (response.status == "success") {
                    // Jika registrasi berhasil
                    registrationStatus.value = "Registrasi berhasil! User ID: ${response.data.userId}"
                } else {
                    // Jika status bukan "success", tampilkan pesan error
                    registrationStatus.value = response.message ?: "Registrasi gagal"
                }
            } catch (e: HttpException) {
                // Menangani error HTTP, misalnya koneksi ke server gagal
                registrationStatus.value = "Error HTTP: ${e.message}"
            } catch (e: Exception) {
                // Menangani error lainnya
                registrationStatus.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                // Menyelesaikan loading state setelah proses selesai
                isLoading.value = false
            }
        }
    }
}
