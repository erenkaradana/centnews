package com.tudio.appcentnews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tudio.appcentnews.models.Article

// Veritabanında yapılacak işlemler için veri erişim nesnesi (DAO).
@Dao
interface DataAccess {
    // Haberi ekler veya zaten haber varsa günceller.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article:Article): Long

    // Tüm haberleri getirir.
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>> //LiveData sayesinde, veritabanındaki değişiklikler anlık otomatik gözlemlenir.

    // Haberi siler. (Favoriler ekranında,istenen haberi favorilerden kaldırmak için gerekli)
    @Delete
    suspend fun deleteArticle(article: Article)
}