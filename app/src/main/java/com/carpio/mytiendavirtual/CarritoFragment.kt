package com.carpio.mytiendavirtual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.carpio.mytiendavirtual.adapter.CarritoDetalleProductoAdapter
import com.carpio.mytiendavirtual.databinding.FragmentCarritoBinding
import com.carpio.mytiendavirtual.models.DetalleProducto
import com.carpio.mytiendavirtual.models.Producto

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CarritoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarritoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCarritoBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rvCarrito.layoutManager = LinearLayoutManager(context)
        binding.rvCarrito.adapter = CarritoDetalleProductoAdapter(
            listOf(
                DetalleProducto(1, Producto(1, 1, "Producto 1", listOf("hola"), "marca",4.2, "producto",2500.0,2559.0, 4.2, 10,"https://helios-i.mashable.com/imagery/articles/04NitPm35IH7OUre3FdqwKB/images-1.fill.size_2000x1366.v1681296423.jpg"), 2, 200.0),
                DetalleProducto(2, Producto(2, 1, "Producto 2", listOf("hola"), "marca", 4.2, "producto",2500.0,2559.0, 4.2, 10,"https://helios-i.mashable.com/imagery/articles/04NitPm35IH7OUre3FdqwKB/images-1.fill.size_2000x1366.v1681296423.jpg"), 1, 150.0),
                DetalleProducto(3, Producto(3, 1, "Producto 3", listOf("hola"), "marca",4.2, "producto",2500.0,2559.0, 4.2, 10,"https://helios-i.mashable.com/imagery/articles/04NitPm35IH7OUre3FdqwKB/images-1.fill.size_2000x1366.v1681296423.jpg"), 1, 300.0),
            )
        )

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CarritoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CarritoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}