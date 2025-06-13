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

        database.getReference("productos").child(id?:"")
            .get().addOnSuccessListener {
            val producto = it.getValue(Producto::class.java)
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
            } else {
                binding.tvNombreProducto.text = "Producto no encontrado"
            }
        }.addOnFailureListener {
            binding.tvNombreProducto.text = "Error al cargar el producto"
        }


        binding.ivImagenBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }


}