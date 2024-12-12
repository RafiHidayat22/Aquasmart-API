package com.example.aquasmart.ui.predict

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquasmart.API.ApiService
import com.example.aquasmart.API.PredictionRequest
import com.example.aquasmart.API.PredictionResponse
import kotlinx.coroutines.launch

class PredictViewModel(private val apiService: ApiService) : ViewModel() {

    private val _predictionResponse = MutableLiveData<PredictionResponse>()
    val predictionResponse: LiveData<PredictionResponse> = _predictionResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    //dataset ikan n
    private val _fishTypes = MutableLiveData<List<String>>()
    val fishTypes: LiveData<List<String>> = _fishTypes

    fun getPrediction(token: String, fishSize: Float, waterCondition: String, fishType: String, feedAmount: Float) {
        viewModelScope.launch {
            try {
                val request = PredictionRequest(fishSize, waterCondition, fishType, feedAmount)
                val response = apiService.prediksiPanen("Bearer $token", request)
                if (response != null) {
                    _predictionResponse.value = response
                } else {
                    _errorMessage.value = "Response is null"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }
    fun getFishTypes(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getFishTypes() // Panggil API untuk mendapatkan jenis ikan
                if (response.message == "Daftar jenis ikan berhasil diambil") {
                    _fishTypes.value = response.data.map { it.name } // Menyimpan nama ikan
                } else {
                    _errorMessage.value = "Gagal mendapatkan data ikan"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", null)
    }
}

