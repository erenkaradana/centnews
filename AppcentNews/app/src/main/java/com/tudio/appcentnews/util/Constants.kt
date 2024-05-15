package com.tudio.appcentnews.util

// Sabit değerler sınıfı.
class Constants {
    companion object{
        const val API_KEY = "5c711d383b1f43c59f1b7a20004af2b4"
        const val BASE_URL = "https://newsapi.org/"
        const val SEARCH_DELAY = 500L //Her karakter girişinden sonra arama yapıp uygulamanın işleyişini aksatmamak için verilen gecikme süresi.
        const val QUERY_SIZE = 20   // B,r sorguda kaç haber (article) nesnesi olacağının sayısı.
    }
}