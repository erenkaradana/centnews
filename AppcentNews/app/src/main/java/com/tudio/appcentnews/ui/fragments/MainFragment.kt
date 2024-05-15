package com.tudio.appcentnews.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tudio.appcentnews.R
import com.tudio.appcentnews.adapters.NewsAdapter
import com.tudio.appcentnews.databinding.FragmentMainBinding
import com.tudio.appcentnews.ui.NewsActivity
import com.tudio.appcentnews.ui.NewsVM
import com.tudio.appcentnews.util.Constants
import com.tudio.appcentnews.util.ItemPadding
import com.tudio.appcentnews.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class   MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var newsVm: NewsVM
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errortext: TextView
    lateinit var binding: FragmentMainBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        // Haber nesneleri (kartları) arasında padding eklemek için kullanılan özel bir utility.
        val itemDecoration = ItemPadding(24)
        binding.recyclerHeadlines.addItemDecoration(itemDecoration)


        // Hata alınması durumunda yeniden dene düğmesi ve hata metni. *Geliştirilmesi gerek*
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_unload, null)

        retryButton = view.findViewById(R.id.retryButton)
        errortext = view.findViewById(R.id.errorText)

        newsVm = (activity as NewsActivity).newsVM

        // Haberler için RecyclerView'ı yapılandır.
        setupHeadlinesRecycler()

        //Haber kartına tıklandığında NewsFragment'a geçiş yapar ve ilgili haberi aktarır.
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_mainFragment_to_articleFragment,bundle)
        }


        // Kullanıcı haber aramak istediğinde TextWatcher kullanarak EditText içeriğini dinler.
        var job: Job? = null
        binding.searchEdit.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {

                if (editable.isNullOrBlank()) {
                    // Kullanıcı arama çubuğunda aradığı metni silerse popüler haberleri geri getirir.
                    newsVm.getHeadlines("us")
                    binding.noContentTextView.visibility = View.GONE
                } else {
                    // Arama çubuğunda metin varken arama yapar.
                    job?.cancel()
                    job = MainScope().launch {
                        delay(Constants.SEARCH_DELAY)
                        editable?.let {
                            if (editable.toString().trim().isNotEmpty()) {
                                newsVm.searchNews(editable.toString())
                            }
                        }
                    }
                }
            }
        })


        // Kullanıcı arama yaptığı zaman sonuçları gözlemler.
        newsVm.searchNews.observe(viewLifecycleOwner, Observer {
                response ->
            when(response){
                is Resource.Success<*> -> {
                    hideProgressBar()
                    if (response.data?.articles.isNullOrEmpty()) {
                        // Arama sonucu yoksa bilgilendirme mesajını görünür yapar. (Kullanıcı aramasını iptal ettikten sonra,
                        // veya mevcut olmayan bir aramayı mevcut ile değiştirdiğinde güncellenmesi için RecyclerView yüklenir. *Geliştirilmesi gerek*
                        binding.noContentTextView.visibility = View.VISIBLE

                        response.data?.let { ApiResponse -> newsAdapter.differ.submitList(ApiResponse.articles.toList())
                            val totalPages = ApiResponse.totalResults / Constants.QUERY_SIZE + 2
                            isLastPage = newsVm.searchNewsPage == totalPages

                            if(isLastPage){
                                binding.recyclerHeadlines.setPadding(0,0,0,0)
                            }
                        }
                    }
                    else{
                        // Sonuçlar varsa RecyclerView'a yükler ve hata mesajı görünür ise bunu görünmez olarak değiştirir.
                        binding.noContentTextView.visibility = View.GONE
                        response.data?.let { ApiResponse -> newsAdapter.differ.submitList(ApiResponse.articles.toList())
                            val totalPages = ApiResponse.totalResults / Constants.QUERY_SIZE + 2
                            isLastPage = newsVm.searchNewsPage == totalPages

                            if(isLastPage){
                                binding.recyclerHeadlines.setPadding(0,0,0,0)
                            }
                        }
                    }
                }
                is Resource.Error<*> ->{
                    // Hata durumunda kullanıcıyı bilgilendir.
                    hideProgressBar()
                    response.message?.let{ message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                    }
                }
                is Resource.Loading<*> ->{
                    // Yükleniyor durumunda progress barı gösterir.
                    showProgressBar()
                }
            }
        })

        //Popüler haberlerin gelmesi için istek gönderildiğinde sonuçları gözlemler.
        newsVm.headlines.observe(viewLifecycleOwner, Observer {
                response ->
            when(response){
                is Resource.Success<*> -> {

                    hideProgressBar()
                    // Haberleri RecyclerView'a atar
                    response.data?.let { ApiResponse -> newsAdapter.differ.submitList(ApiResponse.articles.toList())
                    val totalPages = ApiResponse.totalResults / Constants.QUERY_SIZE + 2

                    isLastPage = newsVm.headlinesPage == totalPages
                        if(isLastPage){
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error<*> ->{
                    hideProgressBar()
                    // Hata durumunda kullanıcıyı bilgilendir.
                    response.message?.let{ message ->
                        Toast.makeText(activity, message,Toast.LENGTH_SHORT).show()

                    }
                }
                is Resource.Loading<*> ->{
                    // Yükleniyor durumunda progress barı göster
                    showProgressBar()
                }
            }
        })
        retryButton.setOnClickListener{
            //Hata durumunda kullanıcı yeniden dene butonuna tıklarsa tekrar istek gönderir.
            newsVm.getHeadlines("us")
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // Progress barı gizle
    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    // Progress barı göster
    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    // RecyclerView için ScrollListener
    val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // Pagination sağlanabilmesi için kullanıcının haberleri kaydırması durumunda gerekli koşullar, sağlanırsa getHeadlines() çağrılır ve haberlerin devamı gösterilir.
            var layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoError = !isError
            val isNotLoadingAndLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_SIZE
            val shouldPaginate = isNoError && isNotLoadingAndLastPage && isLastItem && isNotBegining && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                newsVm.getHeadlines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
        // Haberlerin gösterildiği RecyclerView'ını yapılandır.
        private fun setupHeadlinesRecycler(){
            newsAdapter = NewsAdapter()
            binding.recyclerHeadlines.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@MainFragment.scrollListener)
        }
    }

}