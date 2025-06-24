package com.carpio.mytiendavirtual

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.CarritoDetalleProductoAdapter
import com.carpio.mytiendavirtual.databinding.FragmentCarritoBinding
import com.carpio.mytiendavirtual.models.Carrito
import com.carpio.mytiendavirtual.models.Compra
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.DetalleCompra
import com.carpio.mytiendavirtual.models.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
        binding.BtnPagar.setOnClickListener {
            if (carrito.detalleProductos.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {

                val dialog = AlertDialog.Builder(requireContext()).apply{
                    val linear = LinearLayout(requireContext())
                    linear.orientation = LinearLayout.VERTICAL
                    linear.setPadding(50, 50, 50, 50)
                    val mensaje = TextView(requireContext()).apply { text = "¿Estas Seguro de Pagar la compra?" }
                    linear.addView(mensaje)
                    setTitle("Confirmar Pago")
                    setView(linear)
                    setPositiveButton("Pagar") { _, _ ->
                        // Vaciar variables de carrito guardar carrito en Firebase



                        //Funcionalidad de pago guardado en tabla de pedidos
                        lifecycleScope.launch {
                            val resutadoPago = realizarPagoDeProductos()
                            if(resutadoPago){
                                
                                abrirVentanaMercadoPago(carrito)

                                //Disminuir el stock de los productos comprados
                                disminuirStockProductos(carrito.detalleProductos!!)

                                // Limpiar el carrito
                                carrito.detalleProductos = mutableListOf<DetalleCarrito>()
                                carrito.total = 0.0

                                //Guardar el carrito actualizado en Firebase y cargar la lista
                                guardarCarritoEnFirebase()
                                cargarLista()
                            }
                        }

                    }
                    setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }

                    show()


                }
            }
        }


        return binding.root
    }

    private fun abrirVentanaMercadoPago(carrito: Carrito) {

    }

    private suspend fun disminuirStockProductos(detalleProductos: List<DetalleCarrito>) {

        for (element in detalleProductos){
            //verificar si el producto existe
            val ref = FirebaseDatabase.getInstance().getReference("productos")
                .child(element.productoId.toString())
                .child("stock")

                ref.runTransaction( object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            val stockActual = currentData.getValue(Int::class.java) ?: return Transaction.success(currentData)

                            val nuevoStock = stockActual - element.cantidad!!
                            if(nuevoStock < 0){
                                return Transaction.abort()
                            }
                            currentData.value = nuevoStock
                            return Transaction.success(currentData)
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            if (committed) {
                                Log.d("Firebase", "Stock actualizado correctamente")
                            } else {
                                Log.w("Firebase", "No se pudo actualizar el stock: ${error?.message}")
                            }
                        }
                    }
                )
        }
    }

    private fun cargarLista(){
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
        // Guardar el carrito en Firebase cuando el fragmento se pausa
        guardarCarritoEnFirebase()

    }
    private fun guardarCarritoEnFirebase(){
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

    private suspend fun buscarProductoPorId(productoId: String): Producto? = suspendCoroutine { continuation ->
        val ref = FirebaseDatabase.getInstance().getReference("productos").child(productoId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val producto = snapshot.getValue(Producto::class.java)
                    continuation.resume(producto)
                } else {
                    continuation.resume(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resume(null)
            }
        })
    }

    private suspend fun construirDetalleCompras(carrito: Carrito): List<DetalleCompra> = coroutineScope {
        val productosDeCompra = carrito.detalleProductos?.map {  detalle ->
            async {
                val producto = buscarProductoPorId((detalle.productoId ?: "").toString())
                if (producto == null || producto.nombre.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Producto no encontrado: ${detalle.productoId}", Toast.LENGTH_SHORT).show()
                    }
                    null
                } else {
                    DetalleCompra(
                        productoId = producto.id ?: 0,
                        nombreProducto = producto.nombre ?: "",
                        cantidad = detalle.cantidad ?: 0,
                        precioUnitario = producto.precioFinal ?: 0.0,
                        subtotal = detalle.monto
                    )
                }
            }
        } ?: emptyList()

        productosDeCompra.awaitAll().filterNotNull()
    }

    private suspend fun realizarPagoDeProductos(): Boolean {
        val usuario = FirebaseAuth.getInstance().currentUser ?: return false
        val uid = usuario.uid
        val carritoPreCompra = carrito

        val detalleCompras = construirDetalleCompras(carritoPreCompra)
        if (detalleCompras.isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No se pudieron construir los detalles", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val compra = Compra(
            id = "",
            total = carritoPreCompra.total,
            uidUsuario = uid,
            estado = "Pagado",
            fechaCompra = System.currentTimeMillis(),
            detalleCompras = detalleCompras
        )

        return try {
            FirebaseFirestore.getInstance()
                .collection("pedidos")
                .add(compra)
                .await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show()
            }
            true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Error al realizar la compra", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

}