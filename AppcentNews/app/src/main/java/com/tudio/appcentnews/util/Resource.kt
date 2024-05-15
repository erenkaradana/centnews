package com.tudio.appcentnews.util

/**
 * Ağ çağrılarından elde edilen sonuçları temsil etmek için kullanılan Resource sınıfı.
 * Başarılı, hata veya yükleme durumlarını temsil etmek üzere üç alt sınıf içerir.
 */

sealed class Resource<T>(val data: T? = null, val message: String? = null){

    // Başarılı bir sonuç durumunu temsil eder.
    class Success<T>(data: T?): Resource<T>(data)

    // Hata durumunu temsil eder.
    class Error<T>(message: String,data: T?= null): Resource<T>(data,message)

    // Yüklenme durumunu temsil eder.
    class Loading<T>: Resource<T>()

}