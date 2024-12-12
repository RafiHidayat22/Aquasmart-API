package com.example.aquasmart.ui.predict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.API.ApiConfig
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentPredictBinding

class PredictFragment : Fragment() {

    private var _binding: FragmentPredictBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PredictViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictBinding.inflate(inflater, container, false)
        val apiService = ApiConfig.apiService

        val viewModelFactory = PredictViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PredictViewModel::class.java)

        val waterConditions = listOf("Buruk", "Sedang", "Baik")
        val waterConditionAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            waterConditions
        )
        binding.etWatercondition.setAdapter(waterConditionAdapter)

        binding.etWatercondition.setOnClickListener {
            binding.etWatercondition.showDropDown()
        }

        // melihat jenis ikan
        val token = viewModel.getToken(requireContext())
        if (!token.isNullOrEmpty()) {
            viewModel.getFishTypes(token)
        } else {
            Toast.makeText(requireContext(), "Token tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
        }

        // Observasi perubahan
        viewModel.fishTypes.observe(viewLifecycleOwner) { fishTypes ->
            val fishTypesAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                fishTypes
            )
            binding.etJenisikan.setAdapter(fishTypesAdapter)
        }

        binding.etJenisikan.setOnClickListener {
            binding.etJenisikan.showDropDown()
        }

        // Input form handling
        binding.btnInput.setOnClickListener {
            val fishSize = binding.etFishdata.text.toString().toFloatOrNull() ?: 0f
            val waterCondition = binding.etWatercondition.text.toString()
            val fishType = binding.etJenisikan.text.toString()
            val feedAmount = binding.etJmlpakan.text.toString().toFloatOrNull() ?: 0f

            if (token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Token tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            viewModel.getPrediction(token, fishSize, waterCondition, fishType, feedAmount)

            viewModel.predictionResponse.observe(viewLifecycleOwner) { response ->
                binding.progressBar.visibility = View.GONE
                if (response != null) {
                    val bundle = Bundle().apply {
                        putString("predicted_days_to_harvest", response.data.predicted_days_to_harvest)
                        putString("recommended_feed", response.data.recommended_feed)
                    }
                    findNavController().navigate(R.id.action_navigation_predict_to_predictResultFragment, bundle)
                } else {
                    Toast.makeText(requireContext(), "Terjadi kesalahan dalam prediksi", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                if (errorMessage.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}