package im.dacer.kata.ui.flashcard

import android.content.Context
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@ConfigPersistent
class FlashcardPresenter @Inject constructor(@ApplicationContext val context: Context) :
        BasePresenter<FlashcardMvp>(), CardStackView.CardEventListener {

    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var wordDao: WordDao

    var initDis: Disposable? = null

    override fun attachView(mvpView: FlashcardMvp) {
        super.attachView(mvpView)
        initDis = wordDao.loadNotMastered()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mvpView.setWordList(it.toTypedArray())
                }
    }

    override fun detachView() {
        super.detachView()
        initDis?.dispose()
    }

    override fun onCardDragging(percentX: Float, percentY: Float) {
    }

    override fun onCardSwiped(direction: SwipeDirection?) {
    }

    override fun onCardReversed() {
    }

    override fun onCardMovedToOrigin() {
    }

    override fun onCardClicked(index: Int) {
    }
}