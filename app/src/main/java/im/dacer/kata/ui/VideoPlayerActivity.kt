package im.dacer.kata.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import im.dacer.kata.R
import im.dacer.kata.ui.base.BaseTransparentActivity
import kotlinx.android.synthetic.main.video_player_activity.*
import timber.log.Timber

class VideoPlayerActivity : BaseTransparentActivity() {
    override fun layoutId() = R.layout.video_player_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrEmpty()) {
            finish()
            return
        }

        Timber.e(url)
        videoView.setOnPreparedListener { videoView.start() }
        videoView.setVideoURI(Uri.parse(url))
    }

    companion object {
        private const val EXTRA_URL = "extra_url"

        fun getIntent(context: Context, url: String): Intent {
            return Intent(context, VideoPlayerActivity::class.java).putExtra(EXTRA_URL, url)
        }
    }

}