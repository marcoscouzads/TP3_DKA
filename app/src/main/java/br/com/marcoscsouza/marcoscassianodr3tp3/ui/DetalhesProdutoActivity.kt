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
import com.google.firebase.ktx.Firebase

class DetalhesProdutoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private var produto: Produto? = null
    private var produtoId: Long = 0L
    private val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)

        editarProduto()

        deletarProduto()

    }

    private fun deletarProduto() {
        val btDeletar = binding.btDeletar
        btDeletar.setOnClickListener {
            produto?.let { produtoDao.remove(it) }
            finish()
        }
    }

    private fun editarProduto() {
        val btEditar = binding.btEditar
        btEditar.setOnClickListener {

            Intent(this, CadastroProdutoActivity::class.java).apply {
                putExtra(CHAVE_PRODUTO_ID, produtoId)
                startActivity(this)
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

        if (!estaLogado()){
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }

        produto = produtoDao.buscaPorId(produtoId)
        produto?.let {
            with(binding) {
                activityDetalhesProdutoNome.text = it.nome
                activityDetalhesProdutoDescricao.text = it.descricao
            }

        } ?: finish()
    }

    fun estaLogado(): Boolean{
        val userFire: FirebaseUser? = firebaseAuth.currentUser
        if (userFire != null){
            Toast.makeText(this, "Usuário logado: ${userFire.email}", Toast.LENGTH_SHORT).show()
            return true
        }else{
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}