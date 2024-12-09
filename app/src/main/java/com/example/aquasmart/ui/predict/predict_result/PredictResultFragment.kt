package com.example.aquasmart.ui.predict.predict_result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentPredictBinding
import com.example.aquasmart.databinding.FragmentPredictResultBinding

class PredictResultFragment : Fragment() {

    private var _binding: FragmentPredictResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var bubbleAnimator: BubbleAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize bubble animator
        bubbleAnimator = BubbleAnimator(requireContext(), binding.bubbleContainer)
        bubbleAnimator.startAnimating()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bubbleAnimator.stopAnimating() // Stop animation to avoid memory leaks
        _binding = null
    }

}