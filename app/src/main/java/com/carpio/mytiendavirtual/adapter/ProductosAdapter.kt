package com.carpio.mytiendavirtual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.models.Producto

class ProductosAdapter(private val productos: List<Producto>) : Adapter<ProductosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductosViewHolder(layoutInflater.inflate(R.layout.items_producto, parent, false))
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        val item = productos[position]
        holder.render(item)
    }

}