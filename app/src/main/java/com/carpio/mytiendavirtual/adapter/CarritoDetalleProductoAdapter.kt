package com.carpio.mytiendavirtual.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CarritoDetalleProductoAdapter(private val detalleProductos: List<DetalleCarrito>,
                                    private val eliminarProductoCarrito:(detalleCarrito: DetalleCarrito) -> Unit,
                                    private val modificacionDetalleCarrito:(detalleCarrito: DetalleCarrito) -> Unit) : Adapter<CarritoDetalleProductoViewHolder>() {
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
        holder.render(item, {eliminarProductoCarrito(it)}, {modificacionDetalleCarrito(it)})
    }

}