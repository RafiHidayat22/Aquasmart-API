package com.example.aquasmart.ui.water.water_result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentWaterResultBinding

class WaterResultFragment : Fragment() {

    private lateinit var binding: FragmentWaterResultBinding
    private lateinit var waterWaveView: WaterWaveView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterResultBinding.inflate(inflater, container, false)
        waterWaveView = binding.root.findViewById(R.id.waterWaveView)
        waterWaveView.post {
            waterWaveView.startWaveAnimation()
        }

        return binding.root
    }
}