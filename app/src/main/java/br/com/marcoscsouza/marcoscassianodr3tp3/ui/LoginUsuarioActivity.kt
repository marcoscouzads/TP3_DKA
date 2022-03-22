package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.marcoscsouza.marcoscassianodr3tp3.databinding.ActivityLoginUsuarioBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException

class LoginUsuarioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginUsuarioBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Faça login"

        configuraLogar()

        configuraCadastrarActivity()
    }

    private fun configuraCadastrarActivity() {
        val cadastrar = binding.txtCadastrarUsuario
        cadastrar.setOnClickListener {
            val i = Intent(this, CadastroUsuarioActivity::class.java)
            startActivity(i)
        }
    }

    private fun configuraLogar() {
        val emailLogin = binding.loginUsuarioEmail
        val senhaLogin = binding.loginUsuarioSenha

        binding.btnLogarUsuario.setOnClickListener {
            try {
                firebaseAuth.signInWithEmailAndPassword(
                    emailLogin.text.toString(),
                    senhaLogin.text.toString()
                )
                    .addOnSuccessListener {
                        val userFire: FirebaseUser? = firebaseAuth.currentUser
                        val i = Intent(this, ListaProdutosActivity::class.java)
                        startActivity(i)
                        Toast.makeText(this, "Bem vindo ${userFire?.email}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "falha ao logar", Toast.LENGTH_SHORT).show()
                        Log.e("LOGIN", "erro ao logar", it)
                    }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Os campos e-mail e senha devem ser preenchidos!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(estaLogado()){
            val i = Intent(this, ListaProdutosActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    fun estaLogado(): Boolean{
        val userFire: FirebaseUser? = firebaseAuth.currentUser
        if (userFire != null){
            Toast.makeText(this, "Usuário logado ${userFire.email}", Toast.LENGTH_SHORT).show()
            return true
        }else{
            Toast.makeText(this, "Usuário não logado! ", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}