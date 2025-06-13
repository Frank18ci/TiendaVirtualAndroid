package com.carpio.mytiendavirtual.models

data class Carrito(
    val id: Int? = null,
    val detalleProductos: List<DetalleProducto>? = null,
    val total: Double = detalleProductos?.sumOf {
        (it.producto?.precio ?: 0.0) * (it.cantidad ?: 0)
    } ?: 0.0,
    val usuarioId: Int? = null
)
