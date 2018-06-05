package im.dacer.kata.ui.lyric

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.helper.LyricsHelper
import im.dacer.kata.util.helper.SchemeHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_lyric.*

class LyricActivity : BaseTransparentSwipeActivity() {

    private var searchDisposable: Disposable? = null
    private val adapter: LyricAdapter = LyricAdapter()
    private val progressDialog: MaterialDialog by lazy { MaterialDialog.Builder(this).progress(true, 0).build() }
    private var pageIndex = 1
    private var searchKeyWord: String? = null

    override fun layoutId() = R.layout.activity_lyric

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnLoadMoreListener({ loadMore() }, recyclerView)
        lyricEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    searchKeyWord = s.toString()
                    pageIndex = 1
                    topProgressbar.visibility = View.VISIBLE
                    searchDisposable?.dispose()
                    searchDisposable = LyricsHelper.search(s.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                recyclerView.scrollToPosition(0)
                                adapter.setNewData(it.songs.toList())
                                topProgressbar.visibility = View.GONE
                            }, {
                                timberAndToast(it)
                                topProgressbar.visibility = View.GONE
                            })
                }
            }
        })

        adapter.setOnItemClickListener { _, _, pos ->
            adapter.getItem(pos)?.id?.run {
                progressDialog.show()
                searchDisposable = LyricsHelper.getLyric(this)
                        .map {
                            it.split("\n").filter {
                                        !(it.matches(Regex("^\\[(by|ti|ar|al).+]")) ||
                                        it.matches(Regex("^.*作曲.*[:：].+")) ||
                                        it.matches(Regex("^.*(作词|作詞).*[:：].+")))
                            }.joinToString("\n")
                        }
                        .map { it.replace(Regex("^\n+"), "") }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            progressDialog.dismiss()
                            SchemeHelper.startKata(this@LyricActivity, it, alias = adapter.getItem(pos)!!.name)
                        }, { timberAndToast(it) })
            }
        }

        lyricEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (lyricEditText.text.isEmpty()) {
                    searchEtClearBtn.visibility = View.GONE
                } else {
                    searchEtClearBtn.visibility = View.VISIBLE
                }
            }
        })

        searchEtClearBtn.setOnClickListener {
            lyricEditText.text.clear()
            lyricEditText.clearFocus()
            searchEtClearBtn.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        searchDisposable?.dispose()
    }

    private fun loadMore() {
        pageIndex++
        searchKeyWord?.run {
            searchDisposable = LyricsHelper.search(this, pageIndex)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.songs?.isNotEmpty() == true) {
                            adapter.addData(it.songs.toList())
                            adapter.loadMoreComplete()
                        } else {
                            adapter.loadMoreEnd()
                        }
                    }, {
                        timberAndToast(it)
                        adapter.loadMoreFail()
                    })
        }
    }
}
