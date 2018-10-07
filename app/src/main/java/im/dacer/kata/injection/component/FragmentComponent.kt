package im.dacer.kata.injection.component

import dagger.Subcomponent
import im.dacer.kata.injection.PerFragment
import im.dacer.kata.injection.module.FragmentModule
import im.dacer.kata.ui.main.inbox.InboxFragment
import im.dacer.kata.ui.main.news.NewsFragment
import im.dacer.kata.ui.main.wordbook.WordBookFragment

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(fragment: InboxFragment)
    fun inject(fragment: NewsFragment)
    fun inject(fragment: WordBookFragment)
}