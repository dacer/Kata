package im.dacer.kata.ui.main.wordbook

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.ui.flashcard.FlashcardActivity
import im.dacer.kata.util.extension.applyHeight
import im.dacer.kata.util.extension.getNavBarHeight
import im.dacer.kata.util.extension.onRendered
import kotlinx.android.synthetic.main.fragment_word_book.*
import org.jetbrains.anko.support.v4.dimen
import javax.inject.Inject

class WordBookFragment : BaseFragment(), WordBookMvp {
    @Inject lateinit var wordPresenter: WordBookPresenter
    private val wordAdapter = WordAdapter()
    private var changeListMenuItem: MenuItem? = null

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
        val footView = View(activity)
        footView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dimen(R.dimen.item_word_margin))
        wordAdapter.addFooterView(footView)
        wordAdapter.setOnItemClickListener { _, _, pos -> wordPresenter.onWordClicked(pos)}
        wordAdapter.setEmptyView(R.layout.empty_recycler_view)
        wordAdapter.enableSwipeItem()
        wordAdapter.setOnItemSwipeListener(wordPresenter.swipeListener)
        flashcardTv.setOnClickListener { startActivity(Intent(activity, FlashcardActivity::class.java)) }
        exportAnkiIv.setOnClickListener { onClickExportAnki() }
        setHasOptionsMenu(true)

        bottomPadding.onRendered { bottomPadding.applyHeight(getNavBarHeight()) }
    }

    override fun onResume() {
        super.onResume()
        wordPresenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        wordPresenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.work_book, menu)
        changeListMenuItem = menu.findItem(R.id.change_list)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.change_list -> wordPresenter.onClickChangeList()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setChangeListMenuName(name: String) {
        changeListMenuItem?.title = name
    }

    override fun showWords(wordList: List<Word>?) {
        wordAdapter.setNewData(wordList)
        flashcardTv.isEnabled = wordList?.isNotEmpty() == true
    }

    override fun showFlashcardBtn(show: Boolean) {
        flashcardTv.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun getDecorView() = activity!!.window.decorView!!

    fun onClickExportAnki() {

    }
}