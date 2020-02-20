package im.dacer.kata.ui.base

import im.dacer.kata.util.LogUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T : MvpView> : Presenter<T> {

    var mvpView: T? = null
        private set

    private val compositeDisposable = CompositeDisposable()

    protected fun Disposable.addToComposite() : Disposable {
        compositeDisposable.add(this)
        return this
    }

    protected fun Disposable.removeFromComposite() {
        compositeDisposable.remove(this)
    }

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
        compositeDisposable.clear()
    }

    private val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun doOnError(e: Throwable) {
        mvpView?.toastError(e)
        LogUtils.log(e)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")

}