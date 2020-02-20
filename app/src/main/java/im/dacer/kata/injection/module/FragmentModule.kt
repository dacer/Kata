package im.dacer.kata.injection.module

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import im.dacer.kata.injection.qualifier.ActivityContext

@Module
class FragmentModule(private val fragment: Fragment) {

    @Provides
    internal fun providesFragment(): Fragment = fragment

    @Provides
    internal fun provideActivity(): FragmentActivity? = fragment.activity

    @Provides
    @ActivityContext
    internal fun providesContext(): Context? = fragment.context

}