package com.carpio.mytiendavirtual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.carpio.mytiendavirtual.adapter.ProductosAdapter
import com.carpio.mytiendavirtual.databinding.FragmentListaProductosBinding
import com.carpio.mytiendavirtual.models.Categoria
import com.carpio.mytiendavirtual.models.Producto
import com.carpio.mytiendavirtual.models.ProductoDTO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListaProductosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListaProductosFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var productoAdapter: ProductosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListaProductosBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listaProductos = mutableListOf<ProductoDTO>()
        val listaCategorias = mutableListOf<Categoria>()
        binding.rvProductos.layoutManager= LinearLayoutManager(context)
        val database = FirebaseDatabase.getInstance();
        val refCategoria = database.getReference("categorias")
        refCategoria.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(categoriaSnapshot in snapshot.children){
                    val categoria = categoriaSnapshot.getValue(Categoria::class.java)
                    if(categoria != null){
                        listaCategorias.add(categoria)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer categorias: ${error.message}")
            }
        })
        val refProducto = database.getReference("productos")
        refProducto.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(productoSnapshot in snapshot.children){
                    val producto = productoSnapshot.getValue(Producto::class.java)
                    if (producto != null){
                        val nombreCategoria: String = listaCategorias.filter { it.id == producto.categoriaId }.map { it.nombre }.firstOrNull() ?: "Sin Categoria"

                        listaProductos.add(ProductoDTO(producto.id, nombreCategoria, producto.descripcion, producto.etiquetas, producto.marca, producto.porcentajeDescuento, producto.nombre, producto.precio, producto.precioFinal, producto.valoracionPromedio, producto.stock, producto.imagenUrl))
                    }
                }
                productoAdapter = ProductosAdapter(listaProductos)
                binding.rvProductos.adapter = productoAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer productos: ${error.message}")
            }
        })


        binding.svBuscarProducto.setOnQueryTextListener(this)

        return binding.root
    }


    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        productoAdapter.filtrarProducto(p0 ?: "")
        return false
    }
}