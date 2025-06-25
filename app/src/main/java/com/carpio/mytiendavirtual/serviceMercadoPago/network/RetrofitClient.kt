package com.carpio.mytiendavirtual.serviceMercadoPago.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL_LOCAL = "http://26.116.173.0:3000"
    private const val BASE_URL_ENV = "https://backendmitiendavirtual.onrender.com"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_ENV)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}