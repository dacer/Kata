package im.dacer.kata.ui.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import im.dacer.kata.BuildConfig
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import me.drakeet.multitype.Items
import me.drakeet.support.about.*
import org.jetbrains.anko.toast
import java.util.*


class AboutActivity : BaseAboutActivity() {

    private var easterEgg = 0
    private val appPref by lazy { MultiprocessPref(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.drawable.icon)
        icon.setOnClickListener {
            easterEgg++
            if (easterEgg > 5) {
                toast("\uD83E\uDD21")
                appPref.easterEgg = true
            }
        }
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

        items.add(Category(s(R.string.my_other_projects)))
        items.add(NewContributor(R.drawable.pomodoto, s(R.string.pomotodo), s(R.string.pomotodo_desc), "https://pomotodo.com"))
        items.add(NewContributor(R.drawable.kari, s(R.string.simple_pomodoro), s(R.string.simple_pomodoro_desc), "market://details?id=com.dacer.simplepomodoro"))

        items.add(Line())

        items.add(Category("Thanks"))
        items.add(License("URL2io", "", "", "http://www.url2io.com/"))
        items.add(License("Mercury", "", "", "https://mercury.postlight.com/web-parser/"))
        items.add(License("Fireworks icon made by Freepik from www.flaticon.com", "", "", "http://www.freepik.com"))

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

    override fun onActionClick(action: View) {
        super.onActionClick(action)
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
        private const val YOUTUBE_LINK = "https://www.youtube.com/watch?v=2XgRTL_W0Z0"
        private const val YOUKU_LINK = "https://v.youku.com/v_show/id_XMzM5NDMxODU0NA==.html"

        fun getIntroVideoUrl(context: Context):String {
            return if (languageIsCN(context) || localeIsCN() || simIsCN(context)) {
                YOUKU_LINK
            } else {
                YOUTUBE_LINK
            }
        }

        private fun simIsCN(context: Context): Boolean {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var countryIso = tm.simCountryIso
            var isCN = false
            if (!TextUtils.isEmpty(countryIso)) {
                countryIso = countryIso.toUpperCase(Locale.US)
                if (countryIso.contains("CN")) {
                    isCN = true
                }
            }
            return isCN
        }

        private fun languageIsCN(context: Context): Boolean {
            return context.resources.configuration.locale.country == "CN"
        }

        private fun localeIsCN(): Boolean {
            return Locale.getDefault().country == "CN"
        }
    }
}
