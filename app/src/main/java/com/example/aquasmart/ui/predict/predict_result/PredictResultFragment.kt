package com.example.aquasmart.ui.predict.predict_result

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aquasmart.databinding.FragmentPredictResultBinding

class PredictResultFragment : Fragment() {

    private var _binding: FragmentPredictResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var bubbleAnimator: BubbleAnimator

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictResultBinding.inflate(inflater, container, false)

        // Ambil data dari bundle
        val predictedDays = arguments?.getString("predicted_days_to_harvest")
        val recommendedFeed = arguments?.getString("recommended_feed")

        // Pastikan data ada dan update UI
        if (predictedDays != null && recommendedFeed != null) {
            binding.tvOptimalpanen.text = predictedDays
            binding.tvRecommakan.text = "Recommendation Feeds $recommendedFeed"
        } else {
            // Tampilkan pesan error jika data tidak ada
            Toast.makeText(requireContext(), "Data prediksi tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bubbleAnimator = BubbleAnimator(requireContext(), binding.bubbleContainer)
        bubbleAnimator.startAnimating()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bubbleAnimator.stopAnimating()
        _binding = null
    }
}


