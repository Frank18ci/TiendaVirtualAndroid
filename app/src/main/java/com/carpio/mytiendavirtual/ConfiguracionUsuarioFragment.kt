package com.carpio.mytiendavirtual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carpio.mytiendavirtual.data.TemaSql
import com.carpio.mytiendavirtual.databinding.FragmentConfiguracionUsuarioBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class ConfiguracionUsuarioFragment : Fragment() {

    private lateinit var temaSql: TemaSql
    private lateinit var binding: FragmentConfiguracionUsuarioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfiguracionUsuarioBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        temaSql = TemaSql(requireContext())

        val switchTheme = binding.switchTheme

        switchTheme.isChecked = temaSql.getTema() == "oscuro"

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                temaSql.setTema("oscuro")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                temaSql.setTema("claro")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        (requireActivity() as AppCompatActivity).delegate.applyDayNight()

        }
        // Devuelvo la vista pero si pudiera te devolvería este fragment con un beso muah

        return binding.root
    }

}