package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.marcoscsouza.marcoscassianodr3tp3.R
import br.com.marcoscsouza.marcoscassianodr3tp3.adapter.ProdutoAdapter
import br.com.marcoscsouza.marcoscassianodr3tp3.databinding.ActivityListaProdutosBinding
import br.com.marcoscsouza.marcoscassianodr3tp3.db.AppDatabase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ListaProdutosActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private val adapter = ProdutoAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Lista de Produtos"
        rvProdutos()
        botaoFab()
    }

    override fun onResume() {
        super.onResume()

        if (!estaLogado()){
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }

        val db = AppDatabase.instancia(this)
        val produtoDao = db.produtoDao()
        adapter.atualiza(produtoDao.buscaTodos())
    }


    private fun rvProdutos(){
        val rv = binding.rvLista
        rv.adapter = adapter
        adapter.ClicaNoItem = {
            val i = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
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

    fun estaLogado(): Boolean{
        val userFire: FirebaseUser? = firebaseAuth.currentUser
        if (userFire != null){
            Toast.makeText(this, "Usuário logado: ${userFire.email}", Toast.LENGTH_SHORT).show()
            return true
        }else{
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
}