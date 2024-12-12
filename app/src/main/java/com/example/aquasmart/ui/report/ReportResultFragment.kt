package com.example.aquasmart.ui.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.aquasmart.R
import com.example.aquasmart.databinding.FragmentReportResultBinding

class ReportResultFragment : Fragment() {

    private lateinit var binding: FragmentReportResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportResultBinding.inflate(inflater, container, false)

        // Get the data
        val biayaPakan = arguments?.getDouble("biayaPakan") ?: 0.0
        val biayaTenagaKerja = arguments?.getDouble("biayaTenagaKerja") ?: 0.0
        val biayaLainnya = arguments?.getDouble("biayaLainnya") ?: 0.0
        val totalBiayaOperasional = arguments?.getDouble("totalBiayaOperasional") ?: 0.0
        val jumlahIkanDibudidayakan = arguments?.getInt("jumlahIkanDibudidayakan") ?: 0
        val jumlahIkanTerjual = arguments?.getInt("jumlahIkanTerjual") ?: 0
        val hargaJualPerIkan = arguments?.getDouble("hargaJualPerIkan") ?: 0.0
        val totalPendapatan = arguments?.getDouble("totalPendapatan") ?: 0.0
        val keuntungan = arguments?.getDouble("keuntungan") ?: 0.0

        // Update
        binding.tvBiayaPakan.text = "Biaya Pakan: Rp ${biayaPakan.format()}"
        binding.tvBiayaTenagaKerja.text = "Biaya Tenaga Kerja: Rp ${biayaTenagaKerja.format()}"
        binding.tvBiayaLainnya.text = "Biaya Lainnya: Rp ${biayaLainnya.format()}"
        binding.tvTotalBiayaOperasional.text = "Total Biaya Operasional: Rp ${totalBiayaOperasional.format()}"
        binding.tvJumlahIkanDibudidayakan.text = "Jumlah Ikan Dibudidayakan: $jumlahIkanDibudidayakan ekor"
        binding.tvJumlahIkanTerjual.text = "Jumlah Ikan Terjual: $jumlahIkanTerjual ekor"
        binding.tvHargaJualPerIkan.text = "Harga Jual per Ikan: Rp ${hargaJualPerIkan.format()}"
        binding.tvTotalPendapatan.text = "Total Pendapatan: Rp ${totalPendapatan.format()}"
        binding.tvKeuntungan.text = "Keuntungan: Rp ${keuntungan.format()}"

        // Animation
        val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        binding.tvBiayaPakan.startAnimation(slideUpAnimation)
        binding.tvBiayaTenagaKerja.startAnimation(slideUpAnimation)
        binding.tvBiayaLainnya.startAnimation(slideUpAnimation)
        binding.tvTotalBiayaOperasional.startAnimation(slideUpAnimation)
        binding.tvJumlahIkanDibudidayakan.startAnimation(slideUpAnimation)
        binding.tvJumlahIkanTerjual.startAnimation(slideUpAnimation)
        binding.tvHargaJualPerIkan.startAnimation(slideUpAnimation)
        binding.tvTotalPendapatan.startAnimation(slideUpAnimation)
        binding.tvKeuntungan.startAnimation(slideUpAnimation)

        return binding.root
    }
    private fun Double.format(): String {
        return String.format("%,.0f", this)
    }
}
