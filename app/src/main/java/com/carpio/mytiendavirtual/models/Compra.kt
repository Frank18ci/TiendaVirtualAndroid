package com.carpio.mytiendavirtual.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Compra(
    val uid: String = "",
    val uidUsuario: String = "",
    val detalleCompras: List<DetalleCompra> = emptyList(),
    val total: Double = 0.0,
    val fechaCompra: Long = System.currentTimeMillis(),
    val estado: String = ""
) : Parcelable

