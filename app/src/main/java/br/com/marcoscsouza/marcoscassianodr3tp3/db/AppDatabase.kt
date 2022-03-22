package br.com.marcoscsouza.marcoscassianodr3tp3.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Produto::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao

    companion object {
        fun instancia(context: Context) : AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "listagem.db"
            ).allowMainThreadQueries()
                .build()
        }
    }
}