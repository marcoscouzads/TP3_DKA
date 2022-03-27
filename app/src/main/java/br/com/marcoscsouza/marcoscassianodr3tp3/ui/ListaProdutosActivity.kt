package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import br.com.marcoscsouza.marcoscassianodr3tp3.R
import br.com.marcoscsouza.marcoscassianodr3tp3.adapter.ProdutoAdapter
import br.com.marcoscsouza.marcoscassianodr3tp3.databinding.ActivityListaProdutosBinding
import br.com.marcoscsouza.marcoscassianodr3tp3.db.AppDatabase
import br.com.marcoscsouza.marcoscassianodr3tp3.db.Produto
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ListaProdutosActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore
    private val adapter = ProdutoAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Lista de Produtos"
        rvProdutos()
        botaoFab()

        firestore.collection("produtos")
            .addSnapshotListener { s, _ ->
                s?.let { snapshot ->
                    val produtos = mutableListOf<Produto>()

                    for (documento in snapshot.documents) {
                        Log.i("listagem", "Doc find tempo real ${documento.data}")
                        val produtoDocumento = documento.toObject<ProdutoDocumento>()
                        produtoDocumento?.let { produtoDocumentoNaoNulo ->
                            produtos.add(produtoDocumentoNaoNulo.paraProduto(documento.id))
                        }
                    }
                    adapter.atualiza(produtos)
                }
            }

    }

    //    Com retorno LiveData
    private fun buscarTodosFirestoreComLiveData() {
        val liveData: MutableLiveData<List<Produto>> = MutableLiveData<List<Produto>>()
        firestore.collection("produtos")
            .get()
            .addOnSuccessListener {
                //                Busca de informações do firestore
                it?.let { snapshot ->
                    val produtos = mutableListOf<Produto>()
                    for (documento in snapshot.documents) {
                        Log.i("listar", "Doc find ${documento.data}")
                        documento.data?.let { dados ->
                            //                            forma muito perigosa e pouco usada pois ele precisa de muita manutenção
                            val nome: String = dados["nome"] as String
                            val descricao: String = dados["descricao"] as String
                            val produto = Produto(nome = nome, descricao = descricao)
                            produtos.add(produto)
                        }
                    }
                    liveData.value = produtos
                }

            }
            .addOnFailureListener {

            }
    }

    override fun onResume() {
        super.onResume()

        if (!estaLogado()) {
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }

//        buscarTodosFirestore()


    }

    fun buscarTodosFirestore() {
        firestore.collection("produtos")
            .get()
            .addOnSuccessListener {
                //                Busca de informações do firestore
                it?.let { snapshot ->
                    val produtos = mutableListOf<Produto>()

                    for (documento in snapshot.documents) {
                        Log.i("list", "Doc find ${documento.data}")
                        //                        documento.data?.let { dados ->
                        //                            //                            forma muito perigosa e pouco usada pois ele precisa de muita manutenção
                        //                            val nome: String = dados["nome"] as String
                        //                            val descricao: String = dados["descricao"] as String
                        //                            val produto = Produto(nome = nome, descricao = descricao)
                        //                            produtos.add(produto)
                        //                        }
                        val produtoDocumento = documento.toObject<ProdutoDocumento>()
                        produtoDocumento?.let { produtoDocumentoNaoNulo ->
                            produtos.add(produtoDocumentoNaoNulo.paraProduto(documento.id))
                        }
                    }
                    adapter.atualiza(produtos)
                }

            }
    }

    private fun listarTodosFirestore() {
        firestore.collection("produtos")
            .get()
            .addOnSuccessListener {
                //                Busca de informações do firestore
                it?.let { snapshot ->
                    val produtos = mutableListOf<Produto>()

                    for (documento in snapshot.documents) {
                        Log.i("list", "Doc find ${documento.data}")
                        documento.data?.let { dados ->
                            //                            forma muito perigosa e pouco usada pois ele precisa de muita manutenção
                            val nome: String = dados["nome"] as String
                            val descricao: String = dados["descricao"] as String
                            val produto = Produto(nome = nome, descricao = descricao)
                            produtos.add(produto)
                        }
                    }
                    adapter.atualiza(produtos)
                }

            }
    }


    private fun rvProdutos() {
        val rv = binding.rvLista
        rv.adapter = adapter
        adapter.ClicaNoItem = {
            val i = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra("PRODUTO_ID", it.id)
            }
            startActivity(i)
        }
    }

    private fun botaoFab() {
        val fab = binding.fabProduto
        fab.setOnClickListener {
            val intent = Intent(this, CadastroProdutoActivity::class.java)
            startActivity(intent)
        }
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

    // Processo de desceralização
    private class ProdutoDocumento(
        val nome: String = "",
        val descricao: String = ""
    ) {
        fun paraProduto(id: String): Produto {
            return Produto(id = id, nome = nome, descricao = descricao)
        }
    }
}