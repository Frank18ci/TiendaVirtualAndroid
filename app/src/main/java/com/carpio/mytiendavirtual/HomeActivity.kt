package com.carpio.mytiendavirtual

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.carpio.mytiendavirtual.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // TODO: Decirle que lo amo sin que lo note... oh muy tarde ❤️

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = binding.appBar.toolbarHome

        setSupportActionBar(toolbar)
        drawer = binding.main
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        val navigationView: NavigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

//------------Para que se muestre informacion del usuario-------------------------------------------------//
        val headerView = binding.navView.getHeaderView(0)
        val nombreUsuarioTextView = headerView.findViewById<TextView>(R.id.nav_header_textView)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: ""
                        val apellido = document.getString("apellido") ?: ""
                        nombreUsuarioTextView.text = "$nombre $apellido"
                    }
                }
                .addOnFailureListener {
                    nombreUsuarioTextView.text = "Usuario"
                }
        }
//-------------------------LO DI TODO TE AMOOOO  <3--------------------------------------------------------------//

        val navigationBottom = binding.bottomNavigationHome
        navigationBottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_menu_inicio -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fcvHome, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.item_menu_productos -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fcvHome, ListaProductosFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.item_menu_carrito -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fcvHome, CarritoFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_lateral_informacion_usuario -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fcvHome, InformacionUsuarioFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_lateral_pedidos_realizados -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fcvHome, PedidosRealizadosFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_lateral_configuracion -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fcvHome, ConfiguracionUsuarioFragment())
                    .addToBackStack(null)
                    .commit()
            }

            R.id.nav_lateral_salir_de_cuenta -> {
                FirebaseAuth.getInstance().signOut() // Cierra la sesión

                val googleSignIn = GoogleSignIn.getLastSignedInAccount(this)
                if(googleSignIn != null) {
                    //Cerrar sesión de Google si está iniciada
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleSignInClient.revokeAccess().addOnCompleteListener {
                            //Cuando se haya cerrado la sesión de Google, redirige a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    // Si no hay sesión de Google, simplemente redirige a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}