package com.carpio.mytiendavirtual.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.databinding.ItemsCarritoDetalleProductoBinding
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.Producto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarritoDetalleProductoViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsCarritoDetalleProductoBinding.bind(view)

    fun render(detalleProducto: DetalleCarrito){

        val producto =  FirebaseDatabase.getInstance().getReference("productos").child(detalleProducto.productoId.toString()).addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val producto = snapshot.getValue(Producto::class.java)
                    if (producto != null) {
                        Glide.with(binding.ivProducto.context)
                            .load(producto.imagenUrl)
                            .centerCrop()
                            .into(binding.ivProducto)
                        binding.tvProductoNombre.text = producto.nombre
                        binding.tvCantidad.text = detalleProducto.cantidad.toString()
                        binding.tvPrecio.text = producto.precio.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error al obtener producto", error.message)
                }
            }
        )

    }
}