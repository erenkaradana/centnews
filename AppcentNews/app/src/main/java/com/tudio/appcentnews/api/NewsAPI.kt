package com.tudio.appcentnews.api

import com.tudio.appcentnews.models.ApiResponse
import com.tudio.appcentnews.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// API'ye yapılan istekleri tanımlar.
interface NewsAPI {

    // Ülkeye göre popüler haberleri getiren API isteği.
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode: String = "us", // İstenecek popüler haberlerin ülke kodu ("tr" kodlu haberlerde veri eksiği çok fazla).
        @Query("page")
        pageNumber: Int = 1, // Varsayılan sayfa numarası (başlangıç için 1 tanımlandı).
        @Query("apiKey")
        apiKey : String = API_KEY // API anahtarı.
    ): Response<ApiResponse>

    // Tüm haberler içinde arama yapan API isteği.
    @GET("v2/everything")
    suspend fun searchAllNews(
        @Query("q")
        searchQuery: String, // Aranacak haberin parametresi (başlığı).
        @Query("page")
        pageNumber: Int = 1, // Varsayılan sayfa numarası (başlangıç için 1 tanımlandı).
        @Query("apiKey")
        apiKey: String = API_KEY // API anahtarı.
    ): Response<ApiResponse>

}