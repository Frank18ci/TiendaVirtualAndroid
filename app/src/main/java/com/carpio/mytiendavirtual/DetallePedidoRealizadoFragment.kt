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
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.carpio.mytiendavirtual.adapter.DetalleComprasAdapter
import com.carpio.mytiendavirtual.databinding.FragmentDetallePedidoRealizadoBinding
import com.carpio.mytiendavirtual.models.Compra
import com.carpio.mytiendavirtual.serviceMercadoPago.ApiService
import com.carpio.mytiendavirtual.serviceMercadoPago.model.ResponseHttp
import com.carpio.mytiendavirtual.serviceMercadoPago.network.RetrofitClient
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale


class DetallePedidoRealizadoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetallePedidoRealizadoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val compra = arguments?.getParcelable<Compra>("compra")

        binding.tvTotalV.text = "S/. ${compra!!.total}"
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
        binding.tvFechaV.text = sdf.format(Date(compra.fechaCompra))
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetallePedido.adapter = DetalleComprasAdapter(compra.detalleCompras.toMutableList())


        binding.ivImagenBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRealizarPago.setOnClickListener {
            enviarProductosAlServidor(compra)
        }

        return binding.root
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
}