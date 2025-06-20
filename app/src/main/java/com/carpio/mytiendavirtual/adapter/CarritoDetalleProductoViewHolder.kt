package com.carpio.mytiendavirtual.adapter

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.databinding.ItemsCarritoDetalleProductoBinding
import com.carpio.mytiendavirtual.models.Categoria
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CarritoDetalleProductoViewHolder(val view: View) : ViewHolder(view) {
    val binding = ItemsCarritoDetalleProductoBinding.bind(view)

    fun render(detalleProducto: DetalleCarrito, eliminarProducto:(detalleProducto: DetalleCarrito) -> Unit, modificacionDetalleCarrito:(detalleCarrito: DetalleCarrito) -> Unit) {

        val database = FirebaseDatabase.getInstance()
        val listCategory = mutableListOf<Categoria>()
        val refCategory = database.getReference("categorias")
        refCategory.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (catsnapshot in snapshot.children) {
                    val categoria = catsnapshot.getValue(Categoria::class.java)
                    if (categoria != null) {
                        listCategory.add(categoria)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    binding.root.context,
                    "Error al conectar con la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        FirebaseDatabase.getInstance().getReference("productos").child(detalleProducto.productoId.toString()).addValueEventListener(
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
                        binding.tvPrecio.text = "Precio: S/ ${producto.precioFinal}"
                        binding.tvSubtotal.text = "Subtotal: S/ ${detalleProducto.monto}"
                        val nombreCategoria: String = listCategory.filter { it.id == producto.categoriaId }.map { it.nombre }.firstOrNull() ?: "Sin Categoria"
                        val productoDto = ProductoDTO(producto.id, nombreCategoria, producto.descripcion, producto.etiquetas, producto.marca, producto.porcentajeDescuento, producto.nombre, producto.precio, producto.precioFinal, producto.valoracionPromedio, producto.stock, producto.imagenUrl)
                        binding.tvCategoria.text = productoDto.categoria

                        binding.btnSumarCantidad.setOnClickListener {
                            // Verificar si la cantidad solicitada no excede el stock disponible
                            if(detalleProducto.cantidad == null) {
                                Toast.makeText(
                                    binding.root.context,
                                    "Cantidad no válida",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }

                            if (detalleProducto.cantidad!! <= (producto.stock ?: 0)) {

                                // Actualizar el detalle del carrito
                                detalleProducto.cantidad = detalleProducto.cantidad!! + 1

                                //Calcular el subtotal
                                val precio = producto.precioFinal ?: 0.0
                                val cantidad = detalleProducto.cantidad ?: 0
                                val subtotal = precio * cantidad

                                // Actualizar el monto del detalle del carrito
                                detalleProducto.monto = subtotal

                                // Actualizar la vista
                                binding.tvCantidad.text = detalleProducto.cantidad.toString()
                                binding.tvSubtotal.text = "Subtotal: S/ $subtotal"

                                // Llamar a la función que envia la modificación al carrito
                                modificacionDetalleCarrito(detalleProducto)

                            } else {
                                Toast.makeText(
                                    binding.root.context,
                                    "No hay suficientes unidades en stock",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        binding.btnRestarCantidad.setOnClickListener {

                            if (detalleProducto.cantidad!! > 1) {

                                // Actualizar la cantidad de detalle del carrito
                                detalleProducto.cantidad = detalleProducto.cantidad!! - 1

                                //Calcular el subtotal
                                val precio = producto.precioFinal ?: 0.0
                                val cantidad = detalleProducto.cantidad ?: 0
                                val subtotal = precio * cantidad

                                // Actualizar el monto del detalle del carrito
                                detalleProducto.monto = subtotal

                                // Actualizar la vista
                                binding.tvCantidad.text = detalleProducto.cantidad.toString()
                                binding.tvSubtotal.text = "Subtotal: S/ $subtotal"

                                // Llamar a la función que envia la modificación al carrito
                                modificacionDetalleCarrito(detalleProducto)
                            }
                        }


                        binding.btnEliminar.setOnClickListener {
                            eliminarProducto(detalleProducto)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error al obtener producto", error.message)
                }
            }
        )

    }
}