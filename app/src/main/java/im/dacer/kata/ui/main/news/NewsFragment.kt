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
                EasyNews(title = "２人死亡事故 85歳の女に過失運転致死の罪で判決 禁錮２年", news_publication_time = "16時31分", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "７月の電気料金 大手６社が値上げ", news_publication_time = "16時27分", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "公明 石田政調会長「麻生氏の発言理解できない」", news_publication_time = "16時22分", news_web_image_uri = "https://www3.nhk.or.jp/news/html/20180530/K10011458671_1805302002_1805302006_01_02.jpg"),
                EasyNews(title = "時速100キロ以上 けが人相次ぐ イギリス「チーズ転がし祭り」", news_publication_time = "16時01分")
        ))

    }
}