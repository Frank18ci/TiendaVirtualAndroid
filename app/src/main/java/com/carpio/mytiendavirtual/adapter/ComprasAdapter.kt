package com.carpio.mytiendavirtual.adapter

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.carpio.mytiendavirtual.DetallePedidoRealizadoFragment
import com.carpio.mytiendavirtual.DetalleProductoFragment
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.databinding.ItemsCompraBinding
import com.carpio.mytiendavirtual.models.Compra
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class ComprasAdapter(private val listaCompras : MutableList<Compra>) : Adapter<ComprasAdapter.ComprasViewHolder>() {
    class ComprasViewHolder(view: View) : ViewHolder(view) {
        val binding = ItemsCompraBinding.bind(view)
        fun render(compra: Compra){
            binding.textTotal.text = "S/. ${compra.total}"
            binding.tvEstado.text = compra.estado
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvFechaOrden.text = sdf.format(Date(compra.fechaCompra))

            itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putParcelable("compra", compra)
                val detallePedidoRealizadoFragment = DetallePedidoRealizadoFragment()
                detallePedidoRealizadoFragment.arguments = bundle
                val fragmentManager = itemView.context as androidx.fragment.app.FragmentActivity
                fragmentManager.supportFragmentManager.beginTransaction()
                    .replace(R.id.fcvHome, detallePedidoRealizadoFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComprasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ComprasViewHolder(layoutInflater.inflate(R.layout.items_compra, parent, false))
    }

    override fun getItemCount(): Int = listaCompras.size

    override fun onBindViewHolder(holder: ComprasViewHolder, position: Int) {
        val compra = listaCompras[position]
        holder.render(compra)

    }
}