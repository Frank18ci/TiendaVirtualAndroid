package com.carpio.mytiendavirtual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.CarritoDetalleProductoAdapter
import com.carpio.mytiendavirtual.databinding.FragmentCarritoBinding
import com.carpio.mytiendavirtual.models.Carrito
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CarritoFragment : Fragment() {

    lateinit var carrito : Carrito

    lateinit var binding : FragmentCarritoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarritoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rvCarrito.layoutManager = LinearLayoutManager(context)



        cargarLista()


        return binding.root
    }
    fun cargarLista(){
        val usuario = FirebaseAuth.getInstance().currentUser
        var uid = ""
        if (usuario != null) {
            uid = usuario.uid
        }
        val database = FirebaseFirestore.getInstance().collection("carritos")
        database.document(uid).get().addOnSuccessListener {
            if(it != null && it.exists()) {
                carrito = it.toObject(Carrito::class.java)!!
                if (carrito != null) {
                    binding.tvTotal.text = "Total S/. ${carrito.total}"
                    binding.rvCarrito.adapter = carrito.detalleProductos?.let { it1 ->
                        CarritoDetalleProductoAdapter(
                            it1,
                            {
                                val databaseFirestore = FirebaseFirestore.getInstance()
                                databaseFirestore.collection("carritos")
                                    .document(uid)
                                    .update("detalleProductos", FieldValue.arrayRemove(it), "total", FieldValue.increment(-(it.monto)))
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Producto eliminado del carrito",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        cargarLista()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Error al eliminar producto", e.message.toString())
                                        Toast.makeText(
                                            requireContext(),
                                            "Error al eliminar producto del carrito",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            },
                            { detalleCambiado ->
                                carrito.detalleProductos?.let{ detalles ->

                                    val listaModificable = carrito.detalleProductos?.toMutableList()
                                    carrito.total = listaModificable!!.stream().mapToDouble { m -> m.monto ?: 0.0 }.sum()
                                    carrito.detalleProductos = listaModificable

                                    binding.tvTotal.text = "Total S/. ${carrito.total}"
                                }

                            }
                        )
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Carrito vacío",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Carrito no encontrado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Error al cargar el carrito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onPause() {
        super.onPause()

        //Implementar logica de guardado de carrito en Firebase Firestore
        val usuario = FirebaseAuth.getInstance().currentUser
        var uid = ""
        if (usuario != null) {
            uid = usuario.uid
        }
        val database = FirebaseFirestore.getInstance().collection("carritos")
        database.document(uid).set(carrito)
            .addOnSuccessListener {
                Log.d("Carrito", "Carrito guardado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("Carrito", "Error al guardar el carrito: ${e.message}")
            }
    }
}