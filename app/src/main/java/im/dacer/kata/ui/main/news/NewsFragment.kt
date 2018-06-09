package im.dacer.kata.ui.main.news

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import im.dacer.kata.R
import im.dacer.kata.data.model.NewsItem
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.view.PacmanIndicator
import kotlinx.android.synthetic.main.fragment_news.*
import org.jetbrains.anko.dip
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
        newsAdapter.bindToRecyclerView(recyclerView)
        newsAdapter.setOnItemClickListener { _, _, pos ->
            newsPresenter.onNewsItemClicked(newsAdapter.getItem(pos))
        }
        refreshLayout.setOnRefreshListener(newsPresenter)
        refreshLayout.setRefreshView(PacmanIndicator(activity!!),
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context!!.dip(30)))
        newsPresenter.initData()
    }

    override fun showRefreshing(show: Boolean) {
        refreshLayout.setRefreshing(show)
    }

    override fun onDestroy() {
        super.onDestroy()
        newsPresenter.detachView()
    }

    override fun getMyActivity(): Activity? { return activity }

    override fun showData(newsItems: List<NewsItem>) {
        newsAdapter.setNewData(newsItems)
    }

    override fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) { View.VISIBLE } else { View.GONE }
    }
}