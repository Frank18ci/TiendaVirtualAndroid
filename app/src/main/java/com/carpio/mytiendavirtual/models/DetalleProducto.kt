package com.carpio.mytiendavirtual.models

data class DetalleProducto(
    val id: Int? = null,
    val producto: Producto? = null,
    val cantidad: Int? = null,
    val monto: Double = (producto?.precio ?: 0.0) * (cantidad ?: 0)
)
