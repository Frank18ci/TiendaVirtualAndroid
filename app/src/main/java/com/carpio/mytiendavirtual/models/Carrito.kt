package com.carpio.mytiendavirtual.models

data class Carrito(
    val uid: String? = null,
    var detalleProductos: List<DetalleCarrito>? = null,
    var total: Double = 0.0
)
