package im.dacer.kata.ui.lyric

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.helper.LyricsHelper
import im.dacer.kata.util.helper.SchemeHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lyric.*


class LyricActivity : BaseTransparentSwipeActivity() {

    private var searchDisposable: Disposable? = null
    private val adapter: LyricAdapter = LyricAdapter()
    private val progressDialog: MaterialDialog by lazy { MaterialDialog.Builder(this).progress(true, 0).build() }
    private var pageIndex = 1
    private var searchKeyWord: String? = null
    private val appPref by lazy { MultiprocessPref(this) }

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
                searchDisposable = getSearchObservable(this)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            progressDialog.dismiss()
                            SchemeHelper.startKata(this@LyricActivity, it.text,
                                    alias = adapter.getItem(pos)!!.name, voiceUrl = it.voiceUrl)
                        }, {
                            progressDialog.dismiss()
                            timberAndToast(it)
                        })
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
            lyricEditText.requestFocus()
            showKeyboard()
        }

    }

    private fun getSearchObservable(id: Long): Observable<KataInfo> {
        return if (appPref.easterEgg) {
            Observable.zip(LyricsHelper.getLyric(id), LyricsHelper.getMusicUrl(id),
                    BiFunction<String, String, KataInfo> { t, u -> KataInfo(t, u) })
        } else {
            LyricsHelper.getLyric(id).map { KataInfo(it, "") }
        }
    }

    private data class KataInfo(val text: String, val voiceUrl: String?)

    override fun onResume() {
        super.onResume()
        lyricEditText.requestFocus()
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

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}
