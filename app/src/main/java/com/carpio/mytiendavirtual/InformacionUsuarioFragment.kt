package com.carpio.mytiendavirtual

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.carpio.mytiendavirtual.databinding.FragmentInformacionUsuarioBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InformacionUsuarioFragment : Fragment() {

    private lateinit var binding: FragmentInformacionUsuarioBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInformacionUsuarioBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cargarInformacionUsuario()

        binding.btnEditarInformacionUsuario.setOnClickListener {
            mostrarDialogoEditarInformacion()
        }

        return binding.root
    }

    private fun cargarInformacionUsuario() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.tvNombresV.text = document.getString("nombre") ?: ""
                    binding.tvApellidosV.text = document.getString("apellido") ?: ""
                    binding.tvCorreoEV.text = document.getString("correo") ?: ""

                    val timestamp = document.getTimestamp("fechaNacimiento")
                    val fechaFormateada = timestamp?.toDate()?.let {
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        sdf.format(it)
                    } ?: ""
                    binding.tvFechaNV.text = fechaFormateada

                 binding.tvDireccionV.text = document.getString("direccion") ?: ""
                    binding.tvNumeroTV.text = document.getString("numero") ?: ""
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoEditarInformacion() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.form_update_user)
        dialog.setTitle("Actualizar Información")
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tietNombres = dialog.findViewById<TextInputEditText>(R.id.tietNombres)
        val tietApellidos = dialog.findViewById<TextInputEditText>(R.id.tietApellidos)
        val tietFechaNacimiento = dialog.findViewById<TextInputEditText>(R.id.tietFechaNacimiento)
        val tietTelefono = dialog.findViewById<TextInputEditText>(R.id.textInputNumeroV)
        val tietDireccion = dialog.findViewById<TextInputEditText>(R.id.textInputDireccionV)

        val uid = auth.currentUser?.uid ?: return

        // Precargar datos
        firestore.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tietNombres.setText(document.getString("nombre"))
                    tietApellidos.setText(document.getString("apellido"))

                    val timestamp = document.getTimestamp("fechaNacimiento")
                    val fechaFormateada = timestamp?.toDate()?.let {
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        sdf.format(it)
                    } ?: ""
                    tietFechaNacimiento.setText(fechaFormateada)

                    tietTelefono.setText(document.getString("numero"))
                    tietDireccion.setText(document.getString("direccion"))
                }
            }

        // Mostrar DatePicker al tocar el campo de fecha
        tietFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    tietFechaNacimiento.setText(sdf.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        dialog.findViewById<Button>(R.id.btnAceptar).setOnClickListener {
            val fechaTexto = tietFechaNacimiento.text?.toString() ?: ""

            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val fechaDate = try {
                sdf.parse(fechaTexto)
            } catch (e: Exception) {
                null
            }

            if (fechaDate == null) {
                Toast.makeText(context, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevosDatos = mapOf(
                "nombre" to (tietNombres.text?.toString() ?: ""),
                "apellido" to (tietApellidos.text?.toString() ?: ""),
                "fechaNacimiento" to com.google.firebase.Timestamp(fechaDate),
                "numero" to (tietTelefono.text?.toString() ?: ""),
                "direccion" to (tietDireccion.text?.toString() ?: "")
            )

            firestore.collection("usuarios").document(uid)
                .update(nuevosDatos)
                .addOnSuccessListener {
                    Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    cargarInformacionUsuario()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
