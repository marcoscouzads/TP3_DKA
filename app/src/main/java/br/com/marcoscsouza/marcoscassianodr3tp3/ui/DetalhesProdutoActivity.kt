package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.marcoscsouza.marcoscassianodr3tp3.R
import br.com.marcoscsouza.marcoscassianodr3tp3.databinding.ActivityDetalhesProdutoBinding
import br.com.marcoscsouza.marcoscassianodr3tp3.db.AppDatabase
import br.com.marcoscsouza.marcoscassianodr3tp3.db.Produto
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetalhesProdutoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private var produto: Produto? = null
    private val firestore = Firebase.firestore
    private var produtoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        produtoId = intent.getStringExtra("PRODUTO_ID")

        editarProduto()

        deletarProduto()

    }

    private fun deletarProduto() {
        val btDeletar = binding.btDeletar
        btDeletar.setOnClickListener {

            firestore.collection("produtos")
                .document(produtoId.toString())
                .delete()
            finish()
        }
    }

    private fun editarProduto() {
        val btEditar = binding.btEditar
        btEditar.setOnClickListener {

            val i = Intent(
                this,
                CadastroProdutoActivity::class.java
            ).apply {
                putExtra("PRODUTO_ID",produtoId)

            }
            startActivity(i)
            Toast.makeText(this, "carregar id $produtoId", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuuser, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.exitMenu -> {
                Toast.makeText(this, "Usuário deslogado!", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
                val i = Intent(this, LoginUsuarioActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (!estaLogado()) {
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }

        firestore.collection("produtos")
            .document(produtoId.toString())
            .addSnapshotListener { s, _ ->
                s?.let { document ->
                    document.toObject<ProdutoDocumento>()?.paraProduto(document.id)
                        ?.let { produto ->
                            with(binding) {
                                activityDetalhesProdutoNome.text = produto.nome
                                activityDetalhesProdutoDescricao.text = produto.descricao
                            }
                        }
                }
            }
        Toast.makeText(this, "id ${produto?.id}  id ${produtoId.toString()}", Toast.LENGTH_SHORT).show()
    }

    fun estaLogado(): Boolean {
        val userFire: FirebaseUser? = firebaseAuth.currentUser
        if (userFire != null) {
            Toast.makeText(this, "Usuário logado: ${userFire.email}", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    class ProdutoDocumento(
        val nome: String = "",
        val descricao: String = ""
    ) {
        fun paraProduto(id: String): Produto {
            return Produto(id = id, nome = nome, descricao = descricao)
        }
    }

}