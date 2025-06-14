package com.carpio.mytiendavirtual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EtiquetasProductosAdapter(private val listEtiquestas: List<String>) : RecyclerView.Adapter<EtiquetasProductosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EtiquetasProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EtiquetasProductosViewHolder(layoutInflater.inflate(com.carpio.mytiendavirtual.R.layout.item_etiqueta_producto, parent, false))
    }

    override fun getItemCount(): Int = listEtiquestas.size

    override fun onBindViewHolder(holder: EtiquetasProductosViewHolder, position: Int) {
        val item = listEtiquestas[position]
        holder.render(item)
    }


}