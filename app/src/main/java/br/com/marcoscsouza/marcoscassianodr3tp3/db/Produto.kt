package br.com.marcoscsouza.marcoscassianodr3tp3.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Produto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "nome_produto") val nome: String,
    @ColumnInfo(name = "descricao_produto") val descricao: String
) : Parcelable