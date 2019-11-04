package im.dacer.kata.util.action

import android.content.Context

interface Action {
    fun start(context: Context, text: String)
}
