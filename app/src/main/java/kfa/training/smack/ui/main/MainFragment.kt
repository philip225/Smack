package kfa.training.smack.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.socket.client.IO
import io.socket.emitter.Emitter
import kfa.training.smack.Adapters.MessageAdapter
import kfa.training.smack.Controller.App
import kfa.training.smack.Model.Channel
import kfa.training.smack.Model.Message
import kfa.training.smack.R
import kfa.training.smack.services.MessageService
import kfa.training.smack.utilities.BROADCAST_CHANNEL_CHANGED
import kfa.training.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kfa.training.smack.utilities.SOCKET_URL
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Course deviation - this takes the place of the MainActivity for UI operations, in effect
 * this becomes "MainActivity" for some of the functionality.
 * Course deviation - this uses live data model binding, reason:
 * We need to update the UI elements, not a problem normally when inside a fragment.
 * With fragments live data ViewModel UI model interfaces (MVVC methodology) is strongly suggested.
 * Do we have to use MVVC, yes we do to correctly handle the lifecycle of fragment UI elements, that
 * cannot handle their own lifecycle.
 * However we have synthetic imports that resolve this, well no they don't:
 * If you dispose of this fragment, which what happens if you say logout then log in again, the
 * synthetic imports "fall apart"!
 * So accessing mainChannelName directly will fail after this fragment has been disposed of and
 * re-created.
 * How to fix, use live data, view models and observables, which can handle the lifecycle of the
 * fragment. We setup the observable in the onCreate, which gives us access to a freshly inflated
 * root.
 * Then on any change our observable is called and we can update the UI element via this root!
 * Notice that the adapter is not handled, it handles it's own observable updates.
 * But, the messageListView RecyclerView is handled in an observable, since we need to call
 * scrollToPosition() on it.
 */

class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var drawerLayout: DrawerLayout? = null

    private lateinit var messageAdapter: MessageAdapter

    private var selectedChannel: Channel? = null

    val socket = IO.socket(SOCKET_URL)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)

        // Look for changes for the mainChannelName, this has to be setup here in
        // onCreateView() instead of onViewCreated()
        // We assign our observer to observer changes on mainViewModel.mainChannelName

        mainViewModel.mainChannelName.observe(viewLifecycleOwner, Observer<String> {
            // mainChannelName direct synthetic will no be set, we have to go via root.
            // Doing 'mainChannelName.text = it' will not work.
            root.mainChannelName.text = it
        })

        // The adapter is setup in onViewCreated() and handles it's lifecycle OK, but we need
        // reference to the messageListView, since the RecyclerView UI element is lost on disposal
        // of this fragment.
        mainViewModel.listViewScroll.observe(viewLifecycleOwner, Observer<Int>{
            root.messageListView.scrollToPosition(it)
        })

        updateWithChannel()
        return root
    }

    override fun onResume() {
        // Setup our broadcast receiver to indicate a channel change
        context?.let{
        LocalBroadcastManager.getInstance(it).registerReceiver(onChannelChanged, IntentFilter(
            BROADCAST_CHANNEL_CHANGED
            )
        )}
        super.onResume()
    }

    override fun onDestroy() {
        // Deregister our broadcast manager
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(onChannelChanged)
        }
        super.onDestroy()
    }

    private val onChannelChanged = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            /** Received notification of a channel change **/
            if(App.prefs.isLoggedIn){
                // Update the channel name
                mainViewModel.mainChannelName.value = "#${MessageService.selectedChannel?.name}"
                // Update the channels
                updateWithChannel()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        this.context?.let {
            messageAdapter = MessageAdapter(it, MessageService.messages)
            mainFragment.messageListView.adapter = messageAdapter
            mainFragment.messageListView.layoutManager = LinearLayoutManager(it)
            messageAdapter.notifyDataSetChanged()
        }

        drawerLayout  = activity?.findViewById(R.id.drawer_layout)

        socket.connect()
        socket.on("messageCreated", onNewMessage)


        super.onViewCreated(view, savedInstanceState)
    }


    fun updateWithChannel(){
        /**
         * Change channel layout view main text to the channel name and
         * download the messages, for the channel.
         */
        // Deviation from course, we want to close the draw so we can see the channel list.
        drawerLayout?.closeDrawer(GravityCompat.START)

        if(MessageService.selectedChannel != null){

            MessageService.getMessages(MessageService.selectedChannel!!.id){ complete ->
                if(complete){
                    messageAdapter.notifyDataSetChanged()
                    // Scroll to the bottom
                    if(messageAdapter.itemCount > 0){
                        // Scroll to the last message by index, notice we go via our live data
                        // model view and not directly via the synthetic.
                        mainViewModel.listViewScroll.value = messageAdapter.itemCount-1
                    }
                }
            }
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        /**
         * Socket channel listener, called when channels have changed.
         * Note that this is called on a background worker thread so it does not block the main UI
         * thread. Unlike the Volley library which switches to the UI thread before calling the
         * callbacks, this listener callback does not, so remains on that worker thread.
         */

        // Don't want to accept channels if we are not logged in!
        if(App.prefs.isLoggedIn){

            if(App.prefs.isLoggedIn){
                // We have to run on the UI thread, however the course does not explain why.
                // It is odd since Kotlin singletons are thread safe (suspect this is wrong).
                activity?.runOnUiThread {
                    val channelId = args[2] as String

                    if(channelId == selectedChannel?.id){
                        val channelName = args[0] as String
                        val channelDescription = args[1] as String

                        val newChannel = Channel(channelName, channelDescription, channelId)
                        MessageService.channels.add(newChannel)
                    }
                }
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val msgBody = args[0] as String
            // We skip args[1] since we do not make use of the message id.
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColour = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String

            val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColour,
                id, timeStamp)

            MessageService.messages.add(newMessage)
            messageAdapter.notifyDataSetChanged()
            messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }

    }

}