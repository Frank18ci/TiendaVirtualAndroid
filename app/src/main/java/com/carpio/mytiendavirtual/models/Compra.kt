package com.carpio.mytiendavirtual.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Compra(
    @DocumentId
    val id: String = "",
    val uidUsuario: String = "",
    val detalleCompras: List<DetalleCompra> = emptyList(),
    val total: Double = 0.0,
    val fechaCompra: Long = System.currentTimeMillis(),
    val estado: String = ""
) : Parcelable

