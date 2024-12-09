package com.example.aquasmart.ui.water

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentWaterBinding

class WaterFragment : Fragment() {

    private lateinit var binding: FragmentWaterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWaterBinding.inflate(inflater, container, false)
        binding.btnInput.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_water_to_waterResultFragment)
        }
        binding.fabProfile.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_water_to_profileActivity)

        }

        return binding.root
    }

}