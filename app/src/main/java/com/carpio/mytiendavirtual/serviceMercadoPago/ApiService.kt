package com.carpio.mytiendavirtual.serviceMercadoPago

import com.carpio.mytiendavirtual.serviceMercadoPago.model.ResponseHttp
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("crear_preferencia")
    fun enviarOrdenCompra(@Body requestBody: RequestBody) : Call<ResponseHttp>
}