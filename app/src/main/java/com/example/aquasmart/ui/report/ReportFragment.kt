package com.example.aquasmart.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentReportBinding

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        binding.btnInput.setOnClickListener {
            val biayaPakan = binding.etFeeds.text.toString().toDoubleOrNull() ?: 0.0
            val biayaTenagaKerja = binding.etTenagaKerja.text.toString().toDoubleOrNull() ?: 0.0
            val biayaLainnya = binding.etBiayalain.text.toString().toDoubleOrNull() ?: 0.0
            val jumlahIkanDibudidayakan = binding.etJmlIkanbudidaya.text.toString().toIntOrNull() ?: 0
            val jumlahIkanTerjual = binding.etJmlIkanTerjual.text.toString().toIntOrNull() ?: 0
            val hargaJualPerIkan = binding.etHargaJualPerIkan.text.toString().toDoubleOrNull() ?: 0.0

            // Calculate total
            val totalBiayaOperasional = biayaPakan + biayaTenagaKerja + biayaLainnya
            val totalPendapatan = hargaJualPerIkan * jumlahIkanTerjual
            val keuntungan = totalPendapatan - totalBiayaOperasional

            val reportData = Bundle().apply {
                putDouble("biayaPakan", biayaPakan)
                putDouble("biayaTenagaKerja", biayaTenagaKerja)
                putDouble("biayaLainnya", biayaLainnya)
                putDouble("totalBiayaOperasional", totalBiayaOperasional)
                putInt("jumlahIkanDibudidayakan", jumlahIkanDibudidayakan)
                putInt("jumlahIkanTerjual", jumlahIkanTerjual)
                putDouble("hargaJualPerIkan", hargaJualPerIkan)
                putDouble("totalPendapatan", totalPendapatan)
                putDouble("keuntungan", keuntungan)
            }
            findNavController().navigate(R.id.action_navigation_report_to_reportResultFragment, reportData)
        }

        return binding.root
    }
}