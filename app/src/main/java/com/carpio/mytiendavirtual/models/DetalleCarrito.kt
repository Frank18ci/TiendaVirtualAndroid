package com.carpio.mytiendavirtual.models

data class DetalleCarrito(
    var productoId: Int? = null,
    var cantidad: Int? = null,
    var monto: Double = 0.0
)
