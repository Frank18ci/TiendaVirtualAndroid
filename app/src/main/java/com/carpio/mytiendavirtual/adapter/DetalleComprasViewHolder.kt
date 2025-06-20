package com.carpio.mytiendavirtual.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.carpio.mytiendavirtual.databinding.ItemsDetalleCompraBinding
import com.carpio.mytiendavirtual.models.DetalleCompra

class DetalleComprasViewHolder(view: View) : ViewHolder(view) {
    val binding = ItemsDetalleCompraBinding.bind(view)
    fun render(detalleCompra: DetalleCompra){
        binding.tvNombreProducto.text = detalleCompra.nombreProducto
        binding.tvCantidad.text = "Cantidad: ${detalleCompra.cantidad}"
        binding.tvPrecioUnitario.text = "Precio Unitario: S/. ${detalleCompra.precioUnitario}"
        binding.tvSubTotal.text = "Subtotal: S/. ${detalleCompra.subtotal}"
    }
}