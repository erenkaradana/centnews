package com.tudio.appcentnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.tudio.appcentnews.R
import com.tudio.appcentnews.databinding.FragmentSourceBinding
import com.tudio.appcentnews.models.Article

class NewsSourceFragment : Fragment(R.layout.fragment_source) {

    private lateinit var binding: FragmentSourceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSourceBinding.bind(view)

        // NewsFragment'tan gönderilen URL'yi alır.
        val article = arguments?.getSerializable("articleUrl") as? Article

        article?.let {
            // WebViewClient kullanarak URL'yi WebView'e yükler.
            binding.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(it.url.toString())
            }
        }
        // Geri butonu
        binding.backButton.setOnClickListener{
            getActivity()?.getSupportFragmentManager()?.popBackStack();
        }
    }
}
