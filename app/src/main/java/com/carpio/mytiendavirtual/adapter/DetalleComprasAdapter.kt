package com.carpio.mytiendavirtual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.carpio.mytiendavirtual.R
import com.carpio.mytiendavirtual.models.DetalleCompra

class DetalleComprasAdapter(private val listaDetalleCompra: MutableList<DetalleCompra>) : Adapter<DetalleComprasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleComprasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DetalleComprasViewHolder(layoutInflater.inflate(R.layout.items_detalle_compra, parent, false))
    }

    override fun getItemCount(): Int = listaDetalleCompra.size

    override fun onBindViewHolder(holder: DetalleComprasViewHolder, position: Int) {
        val item = listaDetalleCompra[position]
        holder.render(item)
    }
}