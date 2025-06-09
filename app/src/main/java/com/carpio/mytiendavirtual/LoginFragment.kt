package com.carpio.mytiendavirtual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carpio.mytiendavirtual.databinding.FragmentLoginBinding
import com.carpio.mytiendavirtual.models.Categoria
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values

class LoginFragment : Fragment() {
    lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnCrearCuenta.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainFragment, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnIniciarSesion.setOnClickListener {
            val database = FirebaseDatabase.getInstance();
            val myRef = database.getReference("categoriasssss")
            myRef.child("1").get().addOnSuccessListener { snapshot ->
                val valor = snapshot.getValue(Categoria::class.java)
                Log.d("categoria 1",  valor.toString())
                Toast.makeText(inflater.context, valor.toString(), Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

}