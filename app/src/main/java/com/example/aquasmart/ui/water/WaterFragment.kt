package com.example.aquasmart.ui.water

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.API.ApiConfig
import com.example.aquasmart.API.WaterData
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentWaterBinding
import kotlinx.coroutines.launch

class WaterFragment : Fragment() {

    private lateinit var binding: FragmentWaterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterBinding.inflate(inflater, container, false)

        binding.btnInput.setOnClickListener {
            val ph = binding.etPh.text.toString().toFloatOrNull()
            val turbidity = binding.etTurbidity.text.toString().toFloatOrNull()
            val temperature = binding.etFishdata.text.toString().toFloatOrNull()

            if (ph != null && turbidity != null && temperature != null) {
                sendWaterDataToApi(ph, turbidity, temperature)
            } else {
                Toast.makeText(context, "Mohon isi semua data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


    private fun sendWaterDataToApi(ph: Float, turbidity: Float, temperature: Float) {
        lifecycleScope.launch {
            try {
                // Ambil token dari SharedPreferences
                val sharedPref = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                val token = sharedPref.getString("auth_token", null)

                if (token != null) {
                    // Jika token ada, tambahkan header Authorization
                    Log.d("WaterDataRequest", "Token: Bearer $token")
                    Log.d("WaterDataRequest", "ph: $ph, turbidity: $turbidity, temperature: $temperature")

                    // Buat objek WaterData
                    val waterData = WaterData(ph, turbidity, temperature)

                    // Lakukan request API dengan mengirimkan data dalam format body
                    val response = ApiConfig.apiService.waterPredict(
                        "Bearer $token", // Menggunakan token dengan prefix "Bearer"
                        waterData // Kirim data dalam bentuk objek
                    )

                    // Log response dari server
                    Log.d("WaterDataResponse", "Response: ${response.message}")
                    Log.d("WaterDataResponse", "Prediction: ${response.prediction}")
                    Log.d("WaterDataResponse", "Recommendation: ${response.recommendation}")

                    // Cek apakah ada error
                    if (response.error != null) {
                        Log.e("WaterDataResponse", "Error: ${response.error.error}")
                        Toast.makeText(context, "Error: ${response.error.error}", Toast.LENGTH_SHORT).show()
                    } else {
                        // Menampilkan hasil prediksi dan rekomendasi
                        val bundle = Bundle().apply {
                            putString("prediction", response.prediction)   // Mengirim prediction
                            putString("recommendation", response.recommendation) // Mengirim recommendation
                        }
                        findNavController().navigate(R.id.action_navigation_water_to_waterResultFragment, bundle)
                    }
                } else {
                    Toast.makeText(context, "Token tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("WaterDataRequest", "Terjadi kesalahan: ${e.message}")
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}