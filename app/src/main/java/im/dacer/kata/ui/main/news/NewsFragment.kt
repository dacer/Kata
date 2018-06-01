package im.dacer.kata.ui.main.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.dacer.kata.R
import im.dacer.kata.data.model.NewsItem
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.ui.main.inbox.NewsMvp
import im.dacer.kata.ui.main.inbox.NewsPresenter
import kotlinx.android.synthetic.main.fragment_news.*
import javax.inject.Inject

class NewsFragment: BaseFragment(), NewsMvp {
    override fun layoutId() = R.layout.fragment_news
    @Inject lateinit var newsPresenter: NewsPresenter

    private val newsAdapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        newsPresenter.attachView(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter.openLoadAnimation()
        newsAdapter.bindToRecyclerView(recyclerView)
        newsAdapter.setOnItemClickListener { _, _, pos ->
            val link = newsAdapter.getItem(pos)?.link()
            if (link.isNullOrEmpty()) return@setOnItemClickListener
            activity?.run {
                startService(UrlAnalysisService.getIntent(this, link!!))
            }
        }
        newsPresenter.initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        newsPresenter.detachView()
    }


    override fun showData(newsItems: List<NewsItem>) {
        newsAdapter.setNewData(newsItems)
    }

    override fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) { View.VISIBLE } else { View.GONE }
    }
}