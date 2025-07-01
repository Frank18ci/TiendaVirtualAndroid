package com.carpio.mytiendavirtual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.carpio.mytiendavirtual.databinding.FragmentRegisterBinding
import com.carpio.mytiendavirtual.models.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnIniciarSesion.setOnClickListener {
            registrarUsuario()
        }

        binding.tietFechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }
        binding.tvVolverInicio.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun registrarUsuario() {
        val nombres = binding.tietNombres.text.toString().trim()
        val apellidos = binding.tietApellidos.text.toString().trim()
        val correo = binding.tiCorreo.text.toString().trim()
        val contraseña = binding.tiPassword.text.toString().trim()
        val fechaNac = binding.tietFechaNacimiento.text.toString().trim()

        if (nombres.isBlank() || apellidos.isBlank() || correo.isBlank() ||
            contraseña.isBlank() || fechaNac.isBlank()
        ) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (contraseña.length < 6) {
            Toast.makeText(
                requireContext(),
                "Contraseña mínima de 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaDate = try {
            formato.parse(fechaNac)
        } catch (e: Exception) {
            null
        }
        if (fechaDate == null) {
            Toast.makeText(requireContext(), "Fecha inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val timestampNacimiento = Timestamp(fechaDate)

        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val usuario = Usuario(
                        uid = uid,
                        nombre = nombres,
                        apellido = apellidos,
                        correo = correo,
                        fechaNacimiento = timestampNacimiento,
                        numero = "",
                        direccion= "",
                    )

                    db.collection("usuarios").document(uid).set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Error al guardar en Firestore: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            e.printStackTrace()
                        }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun mostrarDatePicker() {
        val calendario = java.util.Calendar.getInstance()
        val year = calendario.get(java.util.Calendar.YEAR)
        val month = calendario.get(java.util.Calendar.MONTH)
        val day = calendario.get(java.util.Calendar.DAY_OF_MONTH)

        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val fechaSeleccionada = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                binding.tietFechaNacimiento.setText(fechaSeleccionada)
            },
            year,
            month,
            day
        )
        datePicker.show()
    }
}