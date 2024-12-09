package com.example.aquasmart.ui.predict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentPredictBinding

class PredictFragment : Fragment() {

    private var _binding: FragmentPredictBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPredictBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnInput.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_predict_to_predictResultFragment)
        }

        binding.fabProfile.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_predict_to_profileActivity)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}