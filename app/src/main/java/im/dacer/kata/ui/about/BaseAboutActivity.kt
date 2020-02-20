package im.dacer.kata.ui.about

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.support.about.*

abstract class BaseAboutActivity : BaseTransparentSwipeActivity(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.about_page_main_activity

    private var toolbar: Toolbar? = null
    private var collapsingToolbar: CollapsingToolbarLayout? = null

    protected var items: Items = Items()
    private var adapter: MultiTypeAdapter? = null


    protected abstract fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView)
    protected abstract fun onItemsCreated(items: Items)


    protected fun onCreateTitle(): CharSequence? {
        return null
    }


    protected open fun onActionClick(action: View) {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val icon = findViewById<View>(R.id.icon) as ImageView
        val slogan = findViewById<View>(R.id.slogan) as TextView
        val version = findViewById<View>(R.id.version) as TextView
        onCreateHeader(icon, slogan, version)

        collapsingToolbar = findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        val title = onCreateTitle()
        if (title != null) {
            collapsingToolbar!!.title = title
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<View>(R.id.list) as RecyclerView
        onSetupRecyclerView(recyclerView)
    }


    private fun onSetupRecyclerView(recyclerView: RecyclerView) {
        items = Items()
        onItemsCreated(items)
        adapter = MultiTypeAdapter(items)
        adapter!!.register(Category::class.java, CategoryViewProvider())
        adapter!!.register(Card::class.java, CardViewProvider(this))
        adapter!!.register(Line::class.java, LineViewProvider())
        adapter!!.register(Contributor::class.java, ContributorViewProvider())
        adapter!!.register(NewContributor::class.java, NewContributorViewProvider())
        adapter!!.register(License::class.java, LicenseViewProvider())
        recyclerView.adapter = adapter
    }


    override fun setTitle(title: CharSequence) {
        collapsingToolbar!!.title = title
    }


    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.action) {
            onActionClick(v)
        }
    }
}
