package com.carpio.mytiendavirtual.models

data class DetalleCarrito(
    val id: String? = null,
    val productoId: Int? = null,
    val cantidad: Int? = null,
    val monto: Double = 0.0
)
