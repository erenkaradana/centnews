package com.tudio.appcentnews.repository

import com.tudio.appcentnews.api.RetrofitInstance
import com.tudio.appcentnews.database.ArticleDb
import com.tudio.appcentnews.models.Article

// Haber verilerini API'dan alıp işlemek için kullanılan bir repository sınıfı.
class NewsRepository (val db: ArticleDb)  {

    // Ülke kodu ve sayfa numarası kullanarak API'den popüler haberleri alır.
    suspend fun getHeadlines(countryCode: String, pageNumber: Int)
    = RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    // Arama sorgusu ve sayfa numarası kullanarak API'den kullanıcının aradığı metinle eşleşen haberleri arar.
    suspend fun searchNews(searchQuery: String, pageNumber: Int)
    = RetrofitInstance.api.searchAllNews(searchQuery,pageNumber)

    // Bir haberi ekler veya günceller.
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // Favori haberleri getirir.
    fun getFavourites() = db.getArticleDao().getAllArticles()

    // Bir haberi veritabanından siler.
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}