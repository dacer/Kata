package im.dacer.kata.injection.component

import dagger.Subcomponent
import im.dacer.kata.injection.PerActivity
import im.dacer.kata.injection.module.ActivityModule
import im.dacer.kata.ui.FloatActivity
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.bigbang.BigBangActivity
import im.dacer.kata.ui.flashcard.FlashcardActivity
import im.dacer.kata.ui.main.MainActivity
import im.dacer.kata.ui.settings.CacheSettingsActivity
import im.dacer.kata.ui.settings.SettingsActivity
import im.dacer.kata.ui.settings.StyleActivity
import im.dacer.kata.ui.settings.TextAnalysisSettingsActivity
import me.imid.swipebacklayout.lib.app.SwipeBackActivity

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(activity: SwipeBackActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: CacheSettingsActivity)
    fun inject(activity: TextAnalysisSettingsActivity)
    fun inject(activity: StyleActivity)
    fun inject(activity: BigBangActivity)
    fun inject(activity: FloatActivity)
    fun inject(activity: AboutActivity)
    fun inject(activity: FlashcardActivity)

}
