package im.dacer.kata.ui.main.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.dacer.kata.R
import im.dacer.kata.adapter.NewsAdapter
import im.dacer.kata.data.model.EasyNews
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.ui.main.inbox.NewsMvp
import im.dacer.kata.ui.main.inbox.NewsPresenter
import kotlinx.android.synthetic.main.fragment_news.*
import org.jetbrains.anko.support.v4.toast

class NewsFragment: BaseFragment(), NewsMvp {
    private val newsPresenter by lazy { NewsPresenter(context!!, this) }
    private val newsAdapter = NewsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter.openLoadAnimation()
        newsAdapter.bindToRecyclerView(recyclerView)
        newsAdapter.setOnItemClickListener { _, _, pos ->
            toast(pos.toString())
        }
        newsAdapter.setNewData(listOf(
                EasyNews(title = "Title", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "Title1", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "Title2", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "Title3")
        ))

    }
}