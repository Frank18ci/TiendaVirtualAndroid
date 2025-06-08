package com.carpio.mytiendavirtual.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.databinding.ItemsProductoBinding
import com.carpio.mytiendavirtual.models.Producto

class ProductosViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsProductoBinding.bind(view)

    fun render(producto: Producto){

        Glide.with(binding.ivProducto.context).load(producto.imagenUrl).centerCrop().into(binding.ivProducto)

        binding.tvProductoNombre.text = producto.nombre
        binding.tvCategoria.text = producto.categoriaId.toString()
        binding.tvPrecio.text = producto.precio.toString()
        binding.btnInfo.setOnClickListener {  }
    }
}