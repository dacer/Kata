package im.dacer.kata.ui.main.news

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import im.dacer.kata.R
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.view.indicator.PacmanIndicator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_news.*
import org.jetbrains.anko.dip
import javax.inject.Inject


class NewsFragment: BaseFragment(), NewsMvp {
    override fun layoutId() = R.layout.fragment_news

    @Inject lateinit var newsPresenter: NewsPresenter
    @Inject lateinit var settingUtility: SettingUtility

    private val newsAdapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        newsPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        newsPresenter.newsType = NewsType.get(args?.getInt(ARG_NEWS_TYPE))

        recyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter.bindToRecyclerView(recyclerView)
        newsAdapter.setOnItemClickListener { _, _, pos ->
            newsPresenter.onNewsItemClicked(pos, newsAdapter.getItem(pos))
        }
        newsAdapter.setOnItemChildClickListener { _, v, pos ->
            if (v.id == R.id.coverImage) {
                newsPresenter.onPlayVideoClicked(newsAdapter.getItem(pos))
            }
        }
        refreshLayout.setOnRefreshListener(newsPresenter)
        refreshLayout.setRefreshView(PacmanIndicator(activity!!),
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context!!.dip(30)))
        newsPresenter.initData()
    }

    override fun onResume() {
        super.onResume()
        newsAdapter.downloadPicWifiOnly = settingUtility.newsCachingWifiOnly
    }

    override fun showLoadingText(msg: String?) {
        try {
            activity?.runOnUiThread {
                (activity as AppCompatActivity).supportActionBar!!.subtitle = msg
            }
        } catch (t: Throwable) {}
    }

    override fun showRefreshing(show: Boolean) {
        refreshLayout.setRefreshing(show)
    }

    override fun updateItem(index: Int, item: NewsItem) {
        newsAdapter.setData(index, item)
    }

    override fun onDestroy() {
        newsPresenter.detachView()
        showDataDisposable?.dispose()
        super.onDestroy()
    }

    override fun getMyActivity(): Activity? { return activity }

    private var showDataDisposable: Disposable? = null
    override fun showData(newsItems: List<NewsItem>) {
        showDataDisposable?.dispose()
        showDataDisposable = Observable.fromCallable {
            newsItems.sortedByDescending { it.time() }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    newsAdapter.setNewData(it)
                }
    }

    override fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) { View.VISIBLE } else { View.GONE }
    }

    enum class NewsType(val id: Int) {
        NHK_EASY(0), NHK(1);

        companion object {
            fun get(id: Int?) : NewsType {
                for (item in NewsType.values()) {
                    if (item.id == id) return item
                }
                return NewsType.NHK_EASY
            }
        }
    }

    companion object {
        fun newInstance(newsType: NewsType): NewsFragment {
            val args = Bundle()
            args.putInt(ARG_NEWS_TYPE, newsType.id)
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }

        private const val ARG_NEWS_TYPE = "news_type"
    }
}