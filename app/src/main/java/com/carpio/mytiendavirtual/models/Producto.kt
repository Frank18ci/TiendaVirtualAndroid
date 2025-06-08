package com.carpio.mytiendavirtual.models

data class Producto(var id: Int? = null,
                    var categoriaId: Int? = null,
                    var nombre: String? = null,
                    var precio: Double? = null,
                    var imagenUrl: String? = null)
