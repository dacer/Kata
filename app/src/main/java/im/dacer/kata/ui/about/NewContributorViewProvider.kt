package im.dacer.kata.ui.about

import android.content.Intent
import android.net.Uri.parse
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.drakeet.multitype.ItemViewProvider
import me.drakeet.support.about.R


class NewContributorViewProvider : ItemViewProvider<NewContributor, NewContributorViewProvider.ViewHolder>() {

    override fun onCreateViewHolder(
            inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.about_page_item_contributor, parent, false)
        return ViewHolder(root)
    }


    override fun onBindViewHolder(
            holder: ViewHolder, contributor: NewContributor) {
        holder.avatar.setImageResource(contributor.avatarResId)
        holder.name.text = contributor.name
        holder.desc.text = contributor.desc
        holder.url = contributor.url
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var avatar: ImageView
        var name: TextView
        var desc: TextView
        var url: String = ""

        init {
            avatar = itemView.findViewById<View>(R.id.avatar) as ImageView
            name = itemView.findViewById<View>(R.id.name) as TextView
            desc = itemView.findViewById<View>(R.id.desc) as TextView
            itemView.setOnClickListener { v ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = parse(url)
                v.context.startActivity(intent)
            }
        }
    }
}