package com.carpio.mytiendavirtual

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.DetalleComprasAdapter
import com.carpio.mytiendavirtual.databinding.FragmentDetallePedidoRealizadoBinding
import com.carpio.mytiendavirtual.models.Compra
import com.carpio.mytiendavirtual.models.DetalleCarrito
import com.carpio.mytiendavirtual.models.DetalleCompra
import com.carpio.mytiendavirtual.serviceMercadoPago.ApiService
import com.carpio.mytiendavirtual.serviceMercadoPago.model.ResponseHttp
import com.carpio.mytiendavirtual.serviceMercadoPago.network.RetrofitClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale


class DetallePedidoRealizadoFragment : Fragment() {
    private lateinit var binding: FragmentDetallePedidoRealizadoBinding
    private lateinit var idCompra: String
    private var compraCargada: Compra = Compra()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetallePedidoRealizadoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val compra = arguments?.getParcelable<Compra>("compra")
        compraCargada.id = compra!!.id

        cargarValores(compra)






        return binding.root
    }

    fun cargarValores(compra: Compra){
        idCompra = compra.id
        binding.tvTotalV.text = "S/. ${compra.total}"
        binding.tvEstadoV.text = compra.estado


        // Color sucess #388E3C, Color pending #1976D2
        val color: Int
        if(compra.estado == "Pagado"){
            color = ContextCompat.getColor(binding.root.context, R.color.color_success)
            binding.btnRealizarPago.isEnabled = true
        } else{
            color = ContextCompat.getColor(binding.root.context, R.color.color_info)
            binding.btnRealizarPago.isEnabled = true
        }
        binding.tvEstadoV.setTextColor(color)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvFechaV.text = sdf.format(Date(compra!!.fechaCompra))
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetallePedido.adapter = DetalleComprasAdapter(compra!!.detalleCompras.toMutableList())


        binding.ivImagenBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRealizarPago.setOnClickListener {
            enviarProductosAlServidor(compra)
        }
    }

    private fun enviarProductosAlServidor(compra: Compra) {
        val datosEnviar = mapOf(
            "idOrden" to compra.id,
            "costoTotal" to compra.total,
            "productos" to compra.detalleCompras,
            "currentId" to compra.uidUsuario
        )
        println("Mapa de datos para converiter ${datosEnviar}")

        //Convertir a cadena de texto en json
        val gson = Gson()
        val jsonDatos = gson.toJson(datosEnviar)
        println(jsonDatos)

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonDatos.toRequestBody(mediaType)

        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
        val call = apiService.enviarOrdenCompra(requestBody)
        call.enqueue(object: Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if(response.isSuccessful) {
                    println("Orden enviada correctamente")
                    println("Repsuesta del servidor ${response.body()?.message}")
                    println("PreferenceId ${response.body()?.preferenceId}")
                    println("Init point ${response.body()?.init_point}")

                    val init_point = response.body()?.init_point.toString()

                    abrirNavegadorCustomTab(init_point)

                } else if(response.code() == 500){
                    println("Errro en el servidor ${response.errorBody()?.toString()}")
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                println("Ha ocurrido un error en la peticion ${t.message}")
            }
        })
    }

    fun abrirNavegadorCustomTab(init_point: String){
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(init_point))
    }

    override fun onResume() {
        super.onResume()
        Log.d("ONresume", "actioooooooooooooooooooo")
        realizarPeticionObtenerCompra(compraCargada.id)
    }
    private fun disminuirStockProductos(detalleCompra: List<DetalleCompra>) {

        for (element in detalleCompra){
            //verificar si el producto existe
            val ref = FirebaseDatabase.getInstance().getReference("productos")
                .child(element.productoId.toString())
                .child("stock")

            ref.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val stockActual = currentData.getValue(Int::class.java) ?: return Transaction.success(currentData)

                    val nuevoStock = stockActual - element.cantidad
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
    fun realizarPeticionObtenerCompra(compraId: String): Compra?{
        var compra: Compra? = null
        FirebaseFirestore.getInstance().collection("pedidos").document(compraId).get().addOnSuccessListener { snapshot ->
            compra = snapshot.toObject(Compra::class.java)
            compra?.let {
                cargarValores(it)
                disminuirStockProductos(it.detalleCompras)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar compra", Toast.LENGTH_SHORT).show()
        }
        return compra
    }
}