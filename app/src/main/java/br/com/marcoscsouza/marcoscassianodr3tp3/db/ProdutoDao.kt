package br.com.marcoscsouza.marcoscassianodr3tp3.db

//@Dao
interface ProdutoDao {

//    @Query("SELECT * FROM Produto")
    fun buscaTodos() : List<Produto>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun salva(vararg produto: Produto)

//    @Delete
    fun remove(produto: Produto)

//    @Query("SELECT * FROM Produto WHERE id = :id")
    fun buscaPorId(id: String) : Produto?

}