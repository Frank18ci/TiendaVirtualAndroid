package com.carpio.mytiendavirtual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.models.DetalleProducto

class CarritoDetalleProductoAdapter(private val detalleProductos: List<DetalleProducto>) : Adapter<CarritoDetalleProductoViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarritoDetalleProductoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CarritoDetalleProductoViewHolder(layoutInflater.inflate(R.layout.items_carrito_detalle_producto, parent, false))
    }

    override fun getItemCount(): Int = detalleProductos.size

    override fun onBindViewHolder(holder: CarritoDetalleProductoViewHolder, position: Int) {
        val item = detalleProductos[position]
        holder.render(item)
    }

}