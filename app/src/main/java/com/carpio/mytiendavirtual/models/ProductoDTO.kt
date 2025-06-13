package com.carpio.mytiendavirtual.models

data class ProductoDTO(var id: Int? = null,
                       var categoria: String? = null,
                       var descripcion: String? = null,
                       var etiquetas: List<String>? = null,
                       var marca: String? = null,
                       var porcentajeDescuento: Double? = null,
                       var nombre: String? = null,
                       var precio: Double? = null,
                       var precioFinal: Double? = null,
                       var valoracionPromedio: Double? = null,
                       var stock: Int? = null,
                       var imagenUrl: String? = null)
