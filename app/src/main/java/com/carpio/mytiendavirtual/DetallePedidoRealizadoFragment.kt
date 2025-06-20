package com.carpio.mytiendavirtual

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.DetalleComprasAdapter
import com.carpio.mytiendavirtual.databinding.FragmentDetallePedidoRealizadoBinding
import com.carpio.mytiendavirtual.models.Compra
import java.util.Date
import java.util.Locale


class DetallePedidoRealizadoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetallePedidoRealizadoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val compra = arguments?.getParcelable<Compra>("compra")

        binding.tvTotalV.text = "S/. ${compra!!.total}"
        binding.tvEstadoV.text = compra.estado
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvFechaV.text = sdf.format(Date(compra.fechaCompra))
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetallePedido.adapter = DetalleComprasAdapter(compra.detalleCompras.toMutableList())


        binding.ivImagenBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }


}