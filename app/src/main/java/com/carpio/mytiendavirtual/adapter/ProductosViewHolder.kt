package com.carpio.mytiendavirtual.adapter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.DetalleProductoFragment
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.databinding.ItemsProductoBinding
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO

class ProductosViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsProductoBinding.bind(view)

    fun render(producto: ProductoDTO){

        Glide.with(binding.ivProducto.context).load(producto.imagenUrl).centerCrop().into(binding.ivProducto)

        binding.tvProductoNombre.text = producto.nombre
        binding.tvCategoria.text = producto.categoria
        binding.tvPrecio.text = producto.precio.toString()
        itemView.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id", producto.id.toString())
            val detalleProductoFragment = DetalleProductoFragment()
            detalleProductoFragment.arguments = bundle
            val fragmentManager = itemView.context as androidx.fragment.app.FragmentActivity
            fragmentManager.supportFragmentManager.beginTransaction()
                .replace(R.id.fcvHome, detalleProductoFragment)
                .addToBackStack(null)
                .commit()



        }
    }
}