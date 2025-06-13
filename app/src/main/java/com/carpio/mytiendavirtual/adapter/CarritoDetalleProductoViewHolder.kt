package com.carpio.mytiendavirtual.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.databinding.ItemsCarritoDetalleProductoBinding
import com.carpio.mytiendavirtual.models.DetalleProducto

class CarritoDetalleProductoViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsCarritoDetalleProductoBinding.bind(view)

    fun render(detalleProducto: DetalleProducto){
        Glide.with(binding.ivProducto.context)
            .load(detalleProducto.producto?.imagenUrl)
            .centerCrop()
            .into(binding.ivProducto)
        binding.tvProductoNombre.text = detalleProducto.producto?.nombre
        binding.tvCantidad.text = detalleProducto.cantidad.toString()
        binding.tvPrecio.text = detalleProducto.producto?.precio.toString()

    }
}