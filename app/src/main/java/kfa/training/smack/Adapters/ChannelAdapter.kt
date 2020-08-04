package kfa.training.smack.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.Model.Channel
import kfa.training.smack.R
import kotlinx.android.synthetic.main.channel_list.view.*

/**
 * Deviation from course.
 * The channel list uses a recycler view, so we provide a recycler channel adapter for the
 * channel list.
 */
class ChannelAdapter(val context: Context, val drawLayout: DrawerLayout, private val channels: ArrayList<Channel>, val callback: (item: Channel) -> Unit): RecyclerView.Adapter<ChannelAdapter.Holder>() {

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val channelTextView: TextView = itemView.channelNameTxt

        fun bindView(channel: Channel){
            channelTextView.text = context.getString(R.string.channel_name_hash, channel.name)
            channelTextView.setOnClickListener {
                // Close the RH draw!
                drawLayout.closeDrawer(GravityCompat.START)
                callback(channel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.channel_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return channels.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.bindView(channels[position])
    }
}