package com.carpio.mytiendavirtual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.carpio.mytiendavirtual.databinding.FragmentDetalleProductoBinding
import com.carpio.mytiendavirtual.models.Categoria
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.Paint
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.EtiquetasProductosAdapter
import com.carpio.mytiendavirtual.models.Carrito
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.google.common.collect.Lists
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DetalleProductoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetalleProductoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id = arguments?.getString("id")
        val database = FirebaseDatabase.getInstance()
        val listCategory = mutableListOf<Categoria>()
        val refCategory = database.getReference("categorias")
        refCategory.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(catsnapshot in snapshot.children){
                    val categoria = catsnapshot.getValue(Categoria::class.java)
                   if(categoria != null){
                       listCategory.add(categoria)
                   }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Erro al conectar con la base de datos", error.message)
            }
        })

        val ref = database.getReference("productos").child(id?:"")

            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val producto = snapshot.getValue(Producto::class.java)
                    if (producto != null) {
                        val nombreCategoria: String = listCategory.filter { it.id == producto.categoriaId }.map { it.nombre }.firstOrNull() ?: "Sin Categoria"


                        val productoDto = ProductoDTO(producto.id, nombreCategoria, producto.descripcion, producto.etiquetas, producto.marca, producto.porcentajeDescuento, producto.nombre, producto.precio, producto.precioFinal, producto.valoracionPromedio, producto.stock, producto.imagenUrl)

                        binding.tvNombreProducto.text = productoDto.nombre
                        binding.tvPrecio.text = "S/. ${productoDto.precioFinal}"
                        Glide.with(binding.ivProducto.context)
                            .load(productoDto.imagenUrl)
                            .into(binding.ivProducto)
                        binding.tvCategoria.text = productoDto.categoria
                        binding.tvDescripcion.text = productoDto.descripcion ?: "Descripción no disponible"
                        binding.tvDescuentoPorcentaje.text = "${productoDto.porcentajeDescuento}% OFF"
                        binding.tvPrecioAnterior.text = "S/. ${productoDto.precio}"
                        binding.tvPrecioAnterior.paintFlags = binding.tvPrecioAnterior.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                        binding.tvCantidad.text = "Cantidad: ${productoDto.stock ?: 0}"

                        binding.rvEtiquetas.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.rvEtiquetas.adapter = EtiquetasProductosAdapter(productoDto.etiquetas?: listOf())
                        binding.rvEtiquetas.setHasFixedSize(true)

                        binding.tvCantidadElegida.text = 1.toString();
                        binding.tvAumentarCantidadElegida.setOnClickListener {
                            if(binding.tvCantidadElegida.text.toString().toInt() <= (productoDto.stock ?: 0)){
                                binding.tvCantidadElegida.text = (binding.tvCantidadElegida.text.toString().toInt() + 1).toString()
                            }
                        }
                        binding.tvDisminuirCantidadElegida.setOnClickListener {
                            if (binding.tvCantidadElegida.text.toString().toInt() > 1) {
                                binding.tvCantidadElegida.text = (binding.tvCantidadElegida.text.toString().toInt() - 1).toString()
                            }
                        }

                        //Agregar al carrito
                        binding.btnAgregarCarrito.setOnClickListener {
                            val usuario = FirebaseAuth.getInstance().currentUser
                            var uid = ""
                            if (usuario != null) {
                                uid = usuario.uid
                            }
                            val cantidadElegida = binding.tvCantidadElegida.text.toString().toInt()
                            if (cantidadElegida > (productoDto.stock ?: 0)) {
                                Toast.makeText(requireContext(), "No hay suficientes unidades en stock", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            val productoPrecioFinal = productoDto.precioFinal ?: 0.0
                            val montoCarrito = productoPrecioFinal  * cantidadElegida
                            val detalleCarrito = DetalleCarrito(
                                productoId = productoDto.id,
                                cantidad = cantidadElegida,
                                monto = montoCarrito
                            )
                            val carrito = Carrito(
                                uid = uid,
                                detalleProductos = mutableListOf(detalleCarrito),
                                total = (productoDto.precioFinal ?: 0.0) * cantidadElegida,
                            )
                            val databaseFirestore = FirebaseFirestore.getInstance()
                            databaseFirestore.collection("carritos").document(uid)
                                .get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Si el carrito ya existe, actualizamos
                                        databaseFirestore.collection("carritos").document(uid).update(
                                            "detalleProductos", FieldValue.arrayUnion(detalleCarrito),
                                            "total", FieldValue.increment((productoDto.precioFinal ?: 0.0) * cantidadElegida)
                                        )
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Producto agregado al carrito",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        databaseFirestore.collection("carritos").document(uid)
                                            .set(carrito)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Producto agregado al carrito",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }.addOnFailureListener { e ->
                                    Log.e("Error al agregar al carrito", e.message.toString())
                                    Toast.makeText(requireContext(), "Error al agregar al carrito", Toast.LENGTH_SHORT).show()
                                }
                        }

                    } else {
                        binding.tvNombreProducto.text = "Producto no encontrado"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.tvNombreProducto.text = "Error al cargar el producto"
                }
            })
        binding.ivImagenBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }


}