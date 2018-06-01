package im.dacer.kata.injection.module

import android.app.Activity
import android.content.Context

import dagger.Module
import dagger.Provides
import im.dacer.kata.injection.ActivityContext

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    internal fun provideActivity(): Activity = activity

    @Provides
    @ActivityContext
    internal fun providesContext(): Context = activity

}
