package com.tudio.appcentnews.api

import com.tudio.appcentnews.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance sınıfı, Retrofit kütüphanesini kullanarak API'ye erişmek için bir API istemcisi oluşturur.
class RetrofitInstance {

    companion object{

        private val retrofit by lazy{

            // HTTP isteklerini log'a kaydeden bir interceptor oluşturur.
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // OkHttpClient oluşturur ve interceptor eklenir.
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            // BASE_URL'i kullanan bir istemci oluşturur.
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Gson dönüştürücüsünü kullanarak JSON verilerini modele dönüştürür.
                .client(client)
                .build()
        }

        //API servisi oluşturur.
        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}