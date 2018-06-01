package im.dacer.kata.ui.base

interface Presenter<in V : MvpView> {

    fun attachView(mvpView: V)

    fun detachView()
}
