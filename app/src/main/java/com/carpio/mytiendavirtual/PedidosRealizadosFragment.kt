package com.carpio.mytiendavirtual

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.carpio.mytiendavirtual.adapter.ComprasAdapter
import com.carpio.mytiendavirtual.adapter.DetalleComprasAdapter
import com.carpio.mytiendavirtual.databinding.FragmentPedidosRealizadosBinding
import com.carpio.mytiendavirtual.models.Compra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class PedidosRealizadosFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPedidosRealizadosBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rvOrdenes.layoutManager = LinearLayoutManager(requireContext())

        val listaCompras = mutableListOf<Compra>()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("pedidos").whereEqualTo("uidUsuario", uid).get().addOnSuccessListener { snapshot ->
            for(snap in snapshot){
                val compra = snap.toObject(Compra::class.java)
                listaCompras.add(compra)
            }
            if(listaCompras.size > 0){
                binding.rvOrdenes.adapter = ComprasAdapter(listaCompras)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al obtener las ordenes realizadas", Toast.LENGTH_LONG).show()
        }



        return binding.root
    }


}