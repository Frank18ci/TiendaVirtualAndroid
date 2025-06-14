package com.carpio.mytiendavirtual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carpio.mytiendavirtual.databinding.FragmentLoginBinding
import com.carpio.mytiendavirtual.models.Categoria
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.values

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private val RC_SIGN_IN = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in and redirect to HomeActivity
        if(auth.currentUser != null) {
            irAHome()
        }

        googleClient = GoogleSignIn.getClient(
            requireActivity(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )


        binding.btnCrearCuenta.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainContainFragment, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }


        binding.btnIniciarSesion.setOnClickListener { loginConCorreo() }


        binding.btnLoginGoogle.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, RC_SIGN_IN)
        }

        return binding.root
    }


    private fun loginConCorreo() = with(binding) {
        val email = tiCorreo.text.toString().trim()
        val pass  = tiPassword.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tiCorreo.error = "Correo inválido"; return@with
        }
        if (pass.isBlank()) {
            tiPassword.error = "Ingrese contraseña"; return@with
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { irAHome() }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Credenciales incorrectas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) firebaseAuthWithGoogle(account)
                else Toast.makeText(context, "Cuenta Google nula", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                Toast.makeText(context, "Google error code=${e.statusCode}", Toast.LENGTH_LONG).show()
                Log.e("LoginFragment", "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val cred = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(cred)
            .addOnSuccessListener { irAHome() }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Firebase-Google error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun irAHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }
}