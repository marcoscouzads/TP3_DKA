package br.com.marcoscsouza.marcoscassianodr3tp3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.ktx.Firebase

class CadastroProdutoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroProdutoBinding.inflate(layoutInflater)
    }
    private val firebaseAuth = Firebase.auth
    private var produtoId = 0L
    private val produtoDao: ProdutoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.produtoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val btSalvar = binding.btSalvar

        btSalvar.setOnClickListener {
            val novoProduto = produtoCriado()
            produtoDao.salva(novoProduto)
            finish()
        }
        produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }

    override fun onResume() {
        super.onResume()
        if (!estaLogado()){
            val i = Intent(this, LoginUsuarioActivity::class.java)
            startActivity(i)
        }

        produtoDao.buscaPorId(produtoId)?.let {
            title = "Editar Produto"
            binding.cadastroProdutoNome.setText(it.nome)
            binding.cadastroProdutoDescricao.setText(it.descricao)
        }
    }

    private fun produtoCriado(): Produto {

        val campoNome = binding.cadastroProdutoNome
        val nome = campoNome.text.toString()
        val campoDescricao = binding.cadastroProdutoDescricao
        val descricao = campoDescricao.text.toString()

        return Produto(
            id = produtoId,
            nome = nome,
            descricao = descricao
        )
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
}