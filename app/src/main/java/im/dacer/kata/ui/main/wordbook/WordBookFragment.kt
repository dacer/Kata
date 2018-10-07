package im.dacer.kata.ui.main.wordbook

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_word_book.*
import javax.inject.Inject

class WordBookFragment : BaseFragment(), WordBookMvp {
    @Inject lateinit var wordPresenter: WordBookPresenter
    private val wordAdapter = WordAdapter()

    override fun layoutId() = R.layout.fragment_word_book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        wordPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordRecyclerView.layoutManager = LinearLayoutManager(context)

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(wordAdapter)
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        wordAdapter.bindToRecyclerView(wordRecyclerView)
        itemTouchHelper.attachToRecyclerView(wordRecyclerView)
        wordAdapter.setOnItemClickListener { _, _, pos -> wordPresenter.onWordClicked(pos)}
        wordAdapter.setEmptyView(R.layout.empty_recycler_view)
        wordAdapter.enableSwipeItem()
        wordAdapter.setOnItemSwipeListener(wordPresenter.swipeListener)

    }

    override fun onResume() {
        super.onResume()
        wordPresenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        wordPresenter.detachView()
    }
    
    override fun showWords(wordList: List<Word>?) {
        wordAdapter.setNewData(wordList)
    }


    override fun getDecorView() = activity!!.window.decorView!!

}