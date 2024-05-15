package com.tudio.appcentnews.models

// API'den alınan yanıtı temsil eden veri sınıfı.
data class ApiResponse(
    // API yanıtında bulunan haberlerin bir listesi.
    val articles: MutableList<Article>, // Favorilere ekleme gibi kullanıcı etkileşimlerine göre listenin güncellenebilmesi için MutableList kullanılır.

    // API yanıtının durumu (Hata durumunda ilgili hatanın kodu).
    val status: String,

    // API yanıtında toplam kaç haberin olduğunu belirten sayı.
    val totalResults: Int
)