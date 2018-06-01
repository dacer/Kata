package im.dacer.kata.injection.component

import dagger.Subcomponent
import im.dacer.kata.injection.PerActivity
import im.dacer.kata.injection.module.ActivityModule
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.main.MainActivity

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

}
