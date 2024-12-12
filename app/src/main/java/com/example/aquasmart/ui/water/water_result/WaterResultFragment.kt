package com.example.aquasmart.ui.water.water_result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentWaterResultBinding

@Suppress("DEPRECATION")
class WaterResultFragment : Fragment() {

    private lateinit var binding: FragmentWaterResultBinding
    private lateinit var animationResult: AnimationResult

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterResultBinding.inflate(inflater, container, false)

        // Ambil prediction dan recommendation dari arguments
        val prediction = arguments?.getString("prediction")
        val recommendation = arguments?.getString("recommendation")

        // Set prediction ke tvCondition
        binding.tvCondition.text = prediction
        binding.tvRecomendation.text = recommendation

        // Animasi
        animationResult = binding.root.findViewById(R.id.waterWaveView)
        animationResult.post {
            animationResult.startWaveAnimation()
        }

        when (prediction) {
            "Baik" -> binding.tvCondition.setTextColor(resources.getColor(R.color.green))
            "Sedang" -> binding.tvCondition.setTextColor(resources.getColor(R.color.yellow))
            "Buruk" -> binding.tvCondition.setTextColor(resources.getColor(R.color.red))
            else -> binding.tvCondition.setTextColor(resources.getColor(R.color.gray))
        }

        return binding.root
    }
}
