package com.tudio.appcentnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tudio.appcentnews.R
import com.tudio.appcentnews.models.Article

// Adapter sınıfı -> RecyclerView için haber listesini yönetir ve haber nesnesi oluşturur.

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private var onItemClickListener : ((Article) -> Unit)? = null

    // Her bir haber nesnesinin görünümünü temsil eden iç sınıf.
    inner class ArticleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    lateinit var articleImage: ImageView
    lateinit var articleSource: TextView
    lateinit var articleTitle: TextView
    lateinit var articleDescription: TextView
    lateinit var articleDate: TextView

    // Haberler arası farklılık hesaplamak için DiffUtil.ItemCallback implementasyonu.
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    // Haber nesneleri arasındaki farkları izlemek ve güncellemek için AsyncListDiffer.
    val differ = AsyncListDiffer(this,differCallback)

    // Haber nesnesi görünümü oluşturur.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        )
    }

    // Adaptördeki toplam haber sayısını döndürür.
    override fun getItemCount(): Int {
    return differ.currentList.size
    }

    // Konumdaki haber nesnesini görünüme bağlar.
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val article = differ.currentList[position]

        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDate = holder.itemView.findViewById(R.id.articleDateTime)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).apply(RequestOptions.bitmapTransform(
                RoundedCorners(24)
            )).into(articleImage)

            articleSource.text = article.source?.name
            articleTitle.text = article.title
            articleDescription.text = article.description
            articleDate.text = article.publishedAt

            setOnClickListener{
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: ( Article) -> Unit){
        onItemClickListener = listener
    }

}