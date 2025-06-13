package com.carpio.mytiendavirtual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO

class ProductosAdapter(private val productos: List<ProductoDTO>) : Adapter<ProductosViewHolder>() {
    private val productosOriginales = productos.toMutableList()

    fun filtrarProducto(nombre: String){
        if(nombre.isEmpty()){
            productosOriginales.clear()
            productosOriginales.addAll(productos)
        } else {
            productosOriginales.clear()
            productosOriginales.addAll(productos.filter { it.nombre?.contains(nombre, ignoreCase = true) ?: false })

        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductosViewHolder(layoutInflater.inflate(R.layout.items_producto, parent, false))
    }

    override fun getItemCount(): Int = productosOriginales.size

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        val item = productosOriginales[position]
        holder.render(item)
    }


}