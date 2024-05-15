package com.tudio.appcentnews.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tudio.appcentnews.R
import com.tudio.appcentnews.adapters.NewsAdapter
import com.tudio.appcentnews.databinding.FragmentFavoritesBinding
import com.tudio.appcentnews.ui.NewsActivity
import com.tudio.appcentnews.ui.NewsVM
import com.tudio.appcentnews.util.ItemPadding


class FavouritesFragment : Fragment(R.layout.fragment_favorites) {

    lateinit var newsVM: NewsVM
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavoritesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoritesBinding.bind(view)

        // RecyclerView'da haberler arasına padding ekler
        val itemDecoration = ItemPadding(24)
        binding.recyclerFavourites.addItemDecoration(itemDecoration)

        newsVM = (activity as NewsActivity).newsVM

        //RecyclerView'ı yapılandır
        setupFavouritesRecycler()

        // Haber kartına tıklandığında NewsFragment'a geçiş yapar ve ilgili haberi aktarır.
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment,bundle)
        }

        // kaydırma işlemlerini dinle
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                // Haberi favorilerden kaldırır
                newsVM.deleteArticle(article)

                Snackbar.make(view,"Removed from favorites.",Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        newsVM.addToFavourites(article)
                    }
                    show()
                }
            }
        }
        // ItemTouchHelper'ı RecyclerView'a bağlar.
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        //Güncellenme (kullanıcının silme durumu) için favori haberleri izler ve günceller.
        newsVM.getFavourites().observe(viewLifecycleOwner, Observer {
            articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setupFavouritesRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}