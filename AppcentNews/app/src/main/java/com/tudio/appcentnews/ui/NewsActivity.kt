package com.tudio.appcentnews.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tudio.appcentnews.R
import com.tudio.appcentnews.database.ArticleDb
import com.tudio.appcentnews.databinding.ActivityNewsBinding
import com.tudio.appcentnews.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var newsVM: NewsVM
    lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activity'i tam ekran yapar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        // Status barı transparan yapar.
        window.statusBarColor = Color.TRANSPARENT

        // Asenkron işlemler için ViewModelProvider'a repository aktarılır ve ViewModel'ı başlatır.
        val newsRepo = NewsRepository(ArticleDb(this))
        val vmProvider = NewsVMProvider(application, newsRepo)
        newsVM = ViewModelProvider(this, vmProvider).get(NewsVM::class.java)

        // Bottom navigation bar kurulumu.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

}
