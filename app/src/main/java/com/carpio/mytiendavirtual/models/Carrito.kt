package com.carpio.mytiendavirtual.models

data class Carrito(
    val id: String? = null,
    val uid: String? = null,
    val detalleProductos: List<DetalleCarrito>? = null,
    val total: Double = 0.0
)
