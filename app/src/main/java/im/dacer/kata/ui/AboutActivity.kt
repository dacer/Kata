package im.dacer.kata.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import im.dacer.kata.BuildConfig
import im.dacer.kata.R
import me.drakeet.multitype.Items
import me.drakeet.support.about.*
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.Utils
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper

class AboutActivity : me.drakeet.support.about.AboutActivity(), SwipeBackActivityBase {
    private var easterEgg = 0

    private var mHelper: SwipeBackActivityHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper!!.onActivityCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper!!.onPostCreate()
    }


    override fun <T : View?> findViewById(id: Int): T {
        val v = super.findViewById<View>(id)
        return if (v == null && mHelper != null) mHelper?.findViewById(id) as T else v as T
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper!!.swipeBackLayout
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }
    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.drawable.icon)
//        icon.setOnClickListener {
//            easterEgg++
//            if (easterEgg > 5) {
//                toast("\uD83E\uDD21")
//            }
//        }
        slogan.setText(R.string.app_name)
        version.text = BuildConfig.VERSION_NAME
    }

    override fun onItemsCreated(items: Items) {
        items.add(Category(s(R.string.about)))
        items.add(Card(s(R.string.description), s(R.string.review)))

        items.add(Line())

        items.add(Category(s(R.string.developer)))
        items.add(Contributor(R.mipmap.ic_launcher, "Dacer", "頑張ってね (๑•̀ㅂ•́)و✧"))

        items.add(Line())

        items.add(Category("Thanks"))
        items.add(License("URL2io", "", "", "http://www.url2io.com/"))
        items.add(License("Mercury", "", "", "https://mercury.postlight.com/web-parser/"))

        items.add(Category("Licenses"))
        items.add(License("JMdict", "JMdict", CC_LICENSE,
                "http://www.edrdg.org/edrdg/licence.html"))
        items.add(License("Kuromoji", "Kuromoji", License.APACHE_2,
                "https://github.com/atilika/kuromoji"))
        items.add(License("BigBang", "baoyongzhang", License.MIT,
                "https://github.com/baoyongzhang/BigBang"))
        items.add(License("Treasure", "baoyongzhang", License.MIT,
                "https://github.com/baoyongzhang/Treasure"))
        items.add(License("OkHttp", "square", License.APACHE_2,
                "https://github.com/square/okhttp"))
        items.add(License("MultiType", "drakeet", License.APACHE_2,
                "https://github.com/drakeet/MultiType"))
        items.add(License("about-page", "drakeet", License.APACHE_2,
                "https://github.com/drakeet/about-page"))
    }

    override fun onActionClick(action: View?) {
        super.onActionClick(action)
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dacer/Kata"))
//        startActivity(intent)
        goGooglePlay()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun s(resId: Int): String = getString(resId)


    private fun goGooglePlay() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
        }
    }

    companion object {
        private const val CC_LICENSE = "Creative Commons Attribution-ShareAlike Licence (V3.0)"
        const val YOUTUBE_LINK = "https://www.youtube.com/watch?v=2XgRTL_W0Z0"
    }
}
