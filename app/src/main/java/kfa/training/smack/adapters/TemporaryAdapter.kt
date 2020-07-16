package kfa.training.smack.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.R
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.temporary_recycler_layout.view.*

/**
 * Temporary adapter for testing out the RecyclerView, this will be removed on a later commit.
 */
class TemporaryAdapter(private val context: Context, val drawLayout: DrawerLayout, val items: List<String>, val callback: (item: String) -> Unit): RecyclerView.Adapter<TemporaryAdapter.Holder>() {

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        val temporaryTextView = itemView.temporaryTextView

        fun bindView(textItem: String){
            temporaryTextView.text = textItem
            temporaryTextView.setOnClickListener {
                // We have customised the draw, so we need to manually
                // close the draw
                drawLayout.closeDrawer(GravityCompat.START)
                callback(textItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.temporary_recycler_layout, parent, false)
            return Holder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(items[position])
    }

}