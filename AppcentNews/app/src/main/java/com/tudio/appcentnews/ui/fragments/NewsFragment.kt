package com.tudio.appcentnews.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.tudio.appcentnews.R
import com.tudio.appcentnews.databinding.FragmentNewsBinding
import com.tudio.appcentnews.ui.NewsActivity
import com.tudio.appcentnews.ui.NewsVM

class NewsFragment : Fragment(R.layout.fragment_news) {

    lateinit var newsVM : NewsVM
    val args: NewsFragmentArgs by navArgs()
    lateinit var binding: FragmentNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewsBinding.bind(view)

        newsVM = (activity as NewsActivity).newsVM

        // Gönderilmiş tıklanan haber nesnesini alır.
        val article = args.article

        // Haber nesnesinin verilerini alarak ilgili componentlara bağlar.
        val authorTextView: TextView = view.findViewById(R.id.articleSource2)
        val contentImageView: ImageView = view.findViewById(R.id.articleImage2)
        val titleTextView: TextView = view.findViewById(R.id.articleTitle2)
        val dateTextView: TextView = view.findViewById(R.id.articleDateTime2)
        val contentTextView: TextView = view.findViewById(R.id.articleDescription2)

        // Haber var ise ilgili verileri componentlara atar.
        article?.let {

            dateTextView.text = article.publishedAt ?: "No Date Information"

            titleTextView.text = article.title ?: "No Title Information"

            authorTextView.text = article.author ?: "No Author Information"

            contentTextView.text = article.description ?: "No Content Information"


            Glide.with(this).load(article.urlToImage).apply(
                RequestOptions.bitmapTransform(
                RoundedCorners(24)
            )).into(contentImageView)

        // Favorilere ekleme butonu
        binding.fab.setOnClickListener{
            newsVM.addToFavourites(article)
            Snackbar.make(view,"Added favorites.",Snackbar.LENGTH_SHORT).show()
        }
        //Kaynağa git butonu
        binding.goToSource.setOnClickListener {

            val action = NewsFragmentDirections.actionArticleFragmentToSourceFragment(article)
            findNavController().navigate(action)
        }
        // Paylaş butonu
        binding.shareButton.setOnClickListener{

            val url = article.url
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)

            val chooser = Intent.createChooser(intent,"Share using...")
            startActivity(chooser)
        }
        // Geri butonu
        binding.backButton.setOnClickListener{
            getActivity()?.getSupportFragmentManager()?.popBackStack();
        }



    }
}
}


