package com.carpio.mytiendavirtual.adapter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.DetalleProductoFragment
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.databinding.ItemsProductoBinding
import com.carpio.mytiendavirtual.models.Carrito
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProductosViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsProductoBinding.bind(view)

    fun render(producto: ProductoDTO){

        Glide.with(binding.ivProducto.context).load(producto.imagenUrl).centerCrop().into(binding.ivProducto)

        binding.tvProductoNombre.text = producto.nombre
        binding.tvCategoria.text = producto.categoria
        binding.tvPrecio.text = producto.precioFinal.toString()
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
        binding.btnAgregarCarrito.setOnClickListener {
            val usuario = FirebaseAuth.getInstance().currentUser
            var uid = ""
            if (usuario != null) {
                uid = usuario.uid
            }
            val detalleCarrito = DetalleCarrito(
                productoId = producto.id,
                cantidad = 1,
                monto = producto.precioFinal?: 0.0
            )
            val carrito = Carrito(
                uid = uid,
                detalleProductos = mutableListOf(detalleCarrito),
                total = producto.precioFinal ?: 0.0,
            )
            val database = FirebaseFirestore.getInstance()
            database.collection("carritos").document(uid).
            get().addOnSuccessListener { document ->

                if (document.exists()) {
                    // Si el carrito ya existe, actualizamos
                    database.collection("carritos").document(uid).update(
                        "detalleProductos", FieldValue.arrayUnion(detalleCarrito),
                        "total", FieldValue.increment(producto.precioFinal ?: 0.0)
                    )
                        .addOnSuccessListener {
                            Toast.makeText(
                                itemView.context,
                                "Producto agregado al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {
                    database.collection("carritos").document(uid).
                    set(carrito)
                        .addOnSuccessListener {
                            Toast.makeText(
                                itemView.context,
                                "Producto agregado al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    itemView.context,
                    "Error al agregar producto al carrito",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}