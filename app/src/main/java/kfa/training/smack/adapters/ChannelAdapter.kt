package kfa.training.smack.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.Model.Channel
import kotlinx.android.synthetic.main.temporary_recycler_layout.view.*

/**
 * Deviation from course.
 * The channel list uses a recycler view, so we provide a channel adapter for the
 * channel list we get from the MessageService.
 */
class ChannelAdapter(private val context: Context, val drawLayout: DrawerLayout, val channels: ArrayList<Channel>, val callback: (item: String) -> Unit): RecyclerView.Adapter<ChannelAdapter.Holder>() {

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        /*
        val temporaryTextView = itemView.temporaryTextView

        fun bindView(textItem: String){
            temporaryTextView.text = textItem
            temporaryTextView.setOnClickListener {
                // We have customised the draw, so we need to manually
                // close the draw
                drawLayout.closeDrawer(GravityCompat.START)
                callback(textItem)
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("Not yet implemented")
    }
}