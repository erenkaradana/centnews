package com.tudio.appcentnews.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")

// Haber nesnesinin içeriğini temsil eden veri sınıfı.
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source? = null,
    val title: String?,
    val url: String?,
    val urlToImage: String?
): Serializable // Haber nesnelerini veya değişkenlerini fragmentlar arası aktarabilmek için Serializable kullanılır.