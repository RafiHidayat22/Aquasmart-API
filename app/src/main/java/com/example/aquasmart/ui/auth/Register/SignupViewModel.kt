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

    val registrationStatus = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    @RequiresApi(Build.VERSION_CODES.S)
    fun registerUser(
        fullName: String,
        email: String,
        birthDate: String,
        phoneNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.apiService.register(fullName, email, birthDate, phoneNumber, password)
                }

                if (response.status == "success") {
                    registrationStatus.value = "Registrasi berhasil! User ID: ${response.data.userId}"
                } else {
                    registrationStatus.value = response.message ?: "Registrasi gagal"
                }
            } catch (e: HttpException) {
                registrationStatus.value = "Error HTTP: ${e.message}"
            } catch (e: Exception) {
                // Menangani error lainnya
                registrationStatus.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
