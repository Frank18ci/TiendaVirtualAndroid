package com.carpio.mytiendavirtual.models

import com.google.firebase.Timestamp

data class Usuario(

    val uid: String = "",
    var nombre: String = "",
    var apellido: String = "",
    var correo: String = "",
    var fechaNacimiento: Timestamp? = null,
    var direccion: String="",
    var numero: String=""
)
