package com.tudio.appcentnews.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tudio.appcentnews.repository.NewsRepository

/**
 * ViewModel'ı başlatmak için bir ViewModelProvider.Factory uygular.
 * ViewModelProvider tarafından ViewModel oluşturulurken kullanılır.
 */

class NewsVMProvider(val app: Application, val newsRepo: NewsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return NewsVM(app,newsRepo) as T
    }
}