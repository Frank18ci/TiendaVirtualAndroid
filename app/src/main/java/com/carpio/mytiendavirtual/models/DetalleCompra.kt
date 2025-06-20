package com.carpio.mytiendavirtual.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetalleCompra(
    val productoId: Int = 0,
    val nombreProducto: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0
): Parcelable