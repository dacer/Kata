package im.dacer.kata.injection.component

import dagger.Subcomponent
import im.dacer.kata.injection.PerActivity
import im.dacer.kata.injection.module.ActivityModule
import im.dacer.kata.ui.BigBangActivity
import im.dacer.kata.ui.FloatActivity
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.main.MainActivity
import im.dacer.kata.ui.settings.CacheSettingsActivity
import im.dacer.kata.ui.settings.SettingsActivity
import im.dacer.kata.ui.settings.StyleActivity
import me.imid.swipebacklayout.lib.app.SwipeBackActivity

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: SwipeBackActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: SettingsActivity)
    fun inject(mainActivity: CacheSettingsActivity)
    fun inject(mainActivity: StyleActivity)
    fun inject(mainActivity: BigBangActivity)
    fun inject(mainActivity: FloatActivity)

}
