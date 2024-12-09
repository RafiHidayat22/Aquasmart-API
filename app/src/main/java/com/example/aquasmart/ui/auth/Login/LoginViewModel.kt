package com.example.aquasmart.ui.auth.Login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquasmart.API.ApiConfig
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(context: Context, email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.apiService.login(email, password)
                if (response.status == "success") {
                    val token = response.token
                    saveToken(context, token)
                    onSuccess(token)
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                Log.e("Login Error", e.message ?: "Unknown error")
                onError("Login failed: ${e.message}")
            }
        }
    }


    private fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("auth_token", token)  // Simpan token
        editor.apply()
    }
}