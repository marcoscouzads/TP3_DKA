package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.marcoscsouza.marcoscassianodr3tp3.R
import br.com.marcoscsouza.marcoscassianodr3tp3.databinding.ActivityCadastroProdutoBinding
import br.com.marcoscsouza.marcoscassianodr3tp3.db.AppDatabase
import br.com.marcoscsouza.marcoscassianodr3tp3.db.Produto
import br.com.marcoscsouza.marcoscassianodr3tp3.db.ProdutoDao
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CadastroProdutoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroProdutoBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    private var produtoId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val btSalvar = binding.btSalvar

        btSalvar.setOnClickListener {
            val campoNome = binding.cadastroProdutoNome
            val nome = campoNome.text.toString()
            val campoDescricao = binding.cadastroProdutoDescricao
            val descricao = campoDescricao.text.toString()

            val produtoDocumento = ProdutoDocumento(nome = nome, descricao = descricao)
//            val produto = Produto(nome = nome, descricao = descricao)
//            val produtoMapeado = mapOf<String, Any>(
//                "nome" to produto.nome,
//                "descricao" to produto.descricao
//            )
            val colecao = firestore.collection("produtos")
            val documento = produtoId?.let {
                colecao.document(it)
            }?: colecao.document()

//            val documento: DocumentReference = firestore.collection("produtos")
//                .document()

                documento.set(produtoDocumento)

            Log.i("salvar", "produto salvo ${documento.id}")
            val i = Intent(this, ListaProdutosActivity::class.java)
            startActivity(i)


//            val novoProduto = produtoCriado()
//            produtoDao.salva(novoProduto)
//            finish()
        }
        produtoId = intent.getStringExtra("PRODUTO_ID")


    }

    override fun onResume() {
        super.onResume()
        if (!estaLogado()) {
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }
        produtoId = intent.getStringExtra("PRODUTO_ID")
        Toast.makeText(this, "carregar id $produtoId", Toast.LENGTH_SHORT).show()

        firestore.collection("produtos")
            .document(produtoId.toString())
            .addSnapshotListener { s, _ ->
                s?.let { document ->
                    document.toObject<ProdutoDocumento>()
                        ?.paraProduto(document.id)
                        ?.let { produto ->
                            with(binding) {
                                title = "editar activity"
                                cadastroProdutoNome.setText(produto.nome)
                                cadastroProdutoDescricao.setText(produto.descricao)
                            }
                        }
                }
            }


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuuser, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.exitMenu -> {
                Toast.makeText(this, "Usuário deslogado.", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
                val i = Intent(this, LoginUsuarioActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun estaLogado(): Boolean {
        val userFire: FirebaseUser? = firebaseAuth.currentUser
        if (userFire != null) {
            Toast.makeText(this, "Usuário logado: ${userFire.email}", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Toast.makeText(this, "Usuário não está logado!", Toast.LENGTH_SHORT).show()
            return false
        }
    }


    // Processo de desceralização
    class ProdutoDocumento(

        val nome: String = "",
        val descricao: String = ""
    ) {
        fun paraProduto(id: String): Produto {
            return Produto(id = id, nome = nome, descricao = descricao)
        }
    }
}