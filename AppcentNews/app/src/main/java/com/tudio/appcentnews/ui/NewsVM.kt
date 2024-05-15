package com.tudio.appcentnews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tudio.appcentnews.models.ApiResponse
import com.tudio.appcentnews.models.Article
import com.tudio.appcentnews.repository.NewsRepository
import com.tudio.appcentnews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsVM (app : Application, val newsRepo: NewsRepository): AndroidViewModel(app){

    // Haberlerin sonuçlarını güncel tutmak için Listler mutable tanımlanır.
    val headlines: MutableLiveData<Resource<ApiResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: ApiResponse? = null

    val searchNews: MutableLiveData<Resource<ApiResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: ApiResponse? = null

    // Yeni ve eski arama sorgularını izlemek için değişkenler
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    // ViewModel başlatıldığında popüler haberleri alır. (Başlangıç için)
    init{
        getHeadlines("us")
    }
    // Popüler haberleri almak için kullanılan fonksiyon
    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }
    // Arama sonuçlarını almak için kullanılan fonksiyon
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    // Retrofit API çağrılarının başarılı veya başarısız durumunu kontrol eden fonksiyonlar
    private fun handleHeadlineResponse(response: Response<ApiResponse>): Resource<ApiResponse>{
        if(response.isSuccessful){
            // Response başarılı olduğunda API yanıtını işlemek için alır
            response.body()?.let{
                    resultResponse ->
                headlinesPage++
                if (headlinesResponse == null){
                    headlinesResponse = resultResponse
                }
                // Daha önceden veri varsa, yeni gelen verilerle birleştirir
                else{
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll((newArticles))
                }
                // ViewModel'a yeni işlenmiş veriyi döndürür
                return Resource.Success(headlinesResponse ?: resultResponse)
            }
        }
        //Hata durumunda mesajı döndürür
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<ApiResponse>): Resource<ApiResponse>{
        if(response.isSuccessful){
            response.body()?.let{
                    resultResponse ->
                // Yeni arama sorgusu varsa veya ilk çağrıysa sayfa numarasını ve eski arama sorgusunu sıfırlar
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }
                else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                // ViewModel'a yeni işlenmiş veriyi döndürür
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        // Hata durumunda mesajı dödürür
        return Resource.Error(response.message())
    }

    // Favorilere ekleme işlemi
    fun addToFavourites(article: Article) = viewModelScope.launch{
        newsRepo.upsert(article)
    }

    // Favorileri getirme işlemi
    fun getFavourites() = newsRepo.getFavourites()

    // Favorilerden silme işlemi
    fun deleteArticle(article: Article) = viewModelScope.launch{
        newsRepo.deleteArticle(article)
    }

    // İnternet bağlantısını kontrol eden fonksiyon
    fun internetConnection(context: Context): Boolean? {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }?: false
        }
    }

    // Popüler haberleri internetten alma işlemi
    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication()) == true){
                val response = newsRepo.getHeadlines(countryCode,headlinesPage)
                headlines.postValue(handleHeadlineResponse(response))
            }
            else{
                headlines.postValue(Resource.Error("No internet connection."))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("?"))
                else -> headlines.postValue(Resource.Error("?"))
            }
        }
    }

    // Arama sonuçlarını internetten alma işlemi
    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())==true){
                val response = newsRepo.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No internet connection."))
            }
        } catch(t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("?"))
                else -> searchNews.postValue(Resource.Error("?"))
            }
        }
    }

}