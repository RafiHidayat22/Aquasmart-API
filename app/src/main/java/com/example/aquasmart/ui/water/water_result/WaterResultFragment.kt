package com.example.aquasmart.ui.water.water_result

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentWaterBinding
import com.example.aquasmart.databinding.FragmentWaterResultBinding

class WaterResultFragment : Fragment() {

    private lateinit var binding: FragmentWaterResultBinding
    private lateinit var waterWaveView: WaterWaveView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterResultBinding.inflate(inflater, container, false)

        // Mendapatkan referensi ke WaterWaveView
        waterWaveView = binding.root.findViewById(R.id.waterWaveView)

        // Pastikan animasi dimulai setelah layout dibuat
        waterWaveView.post {
            waterWaveView.startWaveAnimation() // Mulai animasi
        }

        return binding.root
    }
}