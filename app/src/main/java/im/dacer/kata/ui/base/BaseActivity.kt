package im.dacer.kata.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.os.Build
import android.support.v4.content.ContextCompat
import im.dacer.kata.R

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}