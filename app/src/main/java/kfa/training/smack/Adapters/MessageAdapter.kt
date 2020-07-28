package kfa.training.smack.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.Model.Message
import kfa.training.smack.R
import kfa.training.smack.services.UserDataService
import kotlinx.android.synthetic.main.message_list_view.view.*

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        return holder.onBindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        // Deviation from course, findViewById is not required any more, itemView is now
        // non-nullable and requires no cast.
        // Lint note, if the variables below are public, the lint complains with:
        // Declaration has type inferred from a platform call, which can lead to unchecked
        // nullability issues. Specify type explicitly as nullable or non-nullable.
        // Which is potentially correct if public, since they can be accessed outside of this class
        // and the parent class, before the synthetic import has assigned objects to them.
        // Setting them private resolves this since they will be available, when this inner class is
        // instantiated.
        private val userImage = itemView.messageUserImage
        private val timeStamp = itemView.timestampLbl
        private val userName = itemView.messageUserNameLbl
        private val messageBody = itemView.messageBodyLbl

        fun onBindMessage(context: Context, message:Message){
            val resourceId = context.resources.getIdentifier(message.userAvatar,
                "drawable", context.packageName)
            userImage.setImageResource(resourceId)
            userImage.setBackgroundColor(
                UserDataService.returnAvatarColour(message.userAvatarColour))
            userName.text = message.userName
            timeStamp.text = message.timeStamp
            messageBody.text = message.message
        }
    }
}