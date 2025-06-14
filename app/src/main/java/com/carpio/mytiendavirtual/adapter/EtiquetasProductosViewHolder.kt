package com.carpio.mytiendavirtual.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.carpio.mytiendavirtual.databinding.ItemEtiquetaProductoBinding

class EtiquetasProductosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemEtiquetaProductoBinding.bind(view)

    fun render(etiqueta: String) {
        binding.tvEtiqueta.text = etiqueta
    }
}