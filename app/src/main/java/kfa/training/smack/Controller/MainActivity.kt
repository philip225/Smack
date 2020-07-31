package kfa.training.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.socket.client.IO
import io.socket.emitter.Emitter
import kfa.training.smack.Model.Channel
import kfa.training.smack.R
import kfa.training.smack.Adapters.ChannelAdapter
import kfa.training.smack.services.AuthService
import kfa.training.smack.services.MessageService
import kfa.training.smack.services.UserDataService
import kfa.training.smack.utilities.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    // Course deviation:
    // selectedChannel has been moved into MessageService singleton so MainFragment can pick it up.
    //private var selectedChannel: Channel? = null

    private lateinit var channelAdapter: ChannelAdapter

    // Course deviation:
    // MessageAdapter has been moved into MainFragment
    // private lateinit var messageAdapter: MessageAdapter

    // Curiously, duplex socket connections are allowed prior to authentication, which is a
    // security issue.
    // See the comment starting "Security test", in this code.
    val socket = IO.socket(SOCKET_URL)

    private fun setupAdapters(){
        /**
         * Setup our adapter, deviation from course, we setup the callback listener here since we
         * are using a recycler view.
         * The callback is simpler than in the course, it returns a Channel object, so we do
         * not need to fish one out from MessageService.
         */

        channelAdapter = ChannelAdapter(
                this, drawerLayout,
                MessageService.channels
            ) { channel ->

            // Callback for a channel that has been clicked (draw has already been closed for us).
            // Update the selected channel.
            MessageService.selectedChannel = channel
            // Course deviation:
            // Broadcast a channel change.
            val channelChange = Intent(BROADCAST_CHANNEL_CHANGED)
            LocalBroadcastManager.getInstance(this).sendBroadcast(channelChange)
            }
        // Set our adapter.
        channel_list.adapter = channelAdapter
        channel_list.layoutManager = LinearLayoutManager(this)
        channel_list.setHasFixedSize(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This is required to provide the draw pop-out toolbar button (three horizontal lines,
        // top left).
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Connect our "WebSocket" socket
        socket.connect()
        // Hook up our socket listener and listen for 'channelCreated' events.
        socket.on("channelCreated", onNewChannel)
        //socket.on("messageCreated", onNewMessage)

        // Notice drawerLayout is now global, this is needed elsewhere.
        drawerLayout  = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, drawerLayout)
        navView.setupWithNavController(navController)

        // Check to see if we are already logged in.
        if (App.prefs.isLoggedIn){
            // Simply calling findUserByEmail will populate the UserDataService
            AuthService.findUserByEmail(this){}
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        // Deviation from course: we do not setup a global listener, it is already setup in
        // setupAdapters()
        // setupAdapters()
    }

    override fun onDestroy() {
        // De-register the user data changed receiver.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangedReceiver)
        // Disconnect the socket.
        socket.disconnect()
        Log.d("SM/SOCKET", "WebSocket disconnected.")
        super.onDestroy()
    }

    private val userDataChangedReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            /**
             * This is called when we receive a broadcast, we will only receive a broadcast from
             * broadcasts sent inside our application for BROADCAST_USER_DATA_CHANGE.
             * BROADCAST_USER_DATA_CHANGE is emitted in code that processes user login and user
             * creation.
             */
            if(App.prefs.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName,
                    "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColour(UserDataService.avatarColour))
                loginBtnNavHeader.text = "Logout"

                // We need some actual channels ('context' is capture closed, hence not using 'it').
                context?.let{
                    MessageService.getChannels { complete->
                        if(complete){
                            // We have zero or more channels to show, we can setup our adapter!
                            // Course deviation, we setup the adapter here since we need to
                            // setup callbacks on initialisation.
                            setupAdapters()

                            // We do not need to notify a change in the data set since we have
                            // just setup the channel adapter.

                            if(MessageService.channels.count() > 0){
                                // We have channels, we default to the first channel.
                                MessageService.selectedChannel = MessageService.channels[0]

                                // Course deviation:
                                // We broadcast to interested parties that the channels
                                // have been updated.
                                val channelChange = Intent(BROADCAST_CHANNEL_CHANGED)
                                LocalBroadcastManager.getInstance(it).sendBroadcast(channelChange)
                            }
                        } else {
                            // ERROR!
                            toasty(context,
                                "Unable to load the channels.")
                        }
                    }
                }
            }
        }
    }

    // updateWithChannel() moved to MainFragment

    override fun onSupportNavigateUp(): Boolean {
        /**
         * Course deviation:
         * This handles navigation for the draw - showing the draw when the tool button is clicked.
         * We cannot use a appBarConfiguration with this since we have removed the menu for our
         * dynamic draw layout.
         * However navigateUp alternatively takes a DrawerLayout object, hence the code amend to
         * allow us access to 'drawerLayout'.
         */
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(drawerLayout) || super.onSupportNavigateUp()
    }

    fun addChannelClicked(view: View) {
       if(App.prefs.isLoggedIn){
           val builder = AlertDialog.Builder(this)
           val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

           // Dialog note, when the positive and negative callbacks exit, the dialog is
           // hidden for us.

           builder.setView(dialogView)
               .setPositiveButton("Add"){ _: DialogInterface?, _: Int ->
                   // Course deviation, parameter 'i' is now named 'which' (both parameters replaced
                   // with '_', since we do not use them).
                   // Course deviation, findViewById is not required any more.
                   val nameTextField = dialogView.addChannelNameTxt
                   val descTextField = dialogView.addChannelDescTxt
                   val channelName = nameTextField.text.toString()
                   val channelDesc = descTextField.text.toString()

                   // Create channel with the channel name and description.
                   // This uses a custom WebSocket like library to achieve full duplex
                   // communication.
                   // Note that parameter order is important, order is channel name then channel
                   // description.
                   socket.emit("newChannel", channelName, channelDesc)

               }
               .setNegativeButton("Cancel"){ dialog: DialogInterface?, which ->
                   // Course deviation, parameter 'i' is now named 'which'

               }.show()
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
                runOnUiThread {
                    val channelId = args[2] as String

                    if(channelId != MessageService.selectedChannel?.id){
                        val channelName = args[0] as String
                        val channelDescription = args[1] as String

                        val newChannel = Channel(channelName, channelDescription, channelId)
                        MessageService.channels.add(newChannel)
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // onNewMessage moved into MainFragment

    fun loginBtnNavClicked(view: View) {
        /**
         * Deviation from course, we are now using navigation to navigate to our login fragment.
         */

        if (App.prefs.isLoggedIn){
            // Logout
            UserDataService.logout()

            // Data has been cleared out so we notify the channel and message adapters of this
            // change, so the channels and messages are cleared.
            // Course deviation: message update is now done via broadcast.
            // Important! If the back end is _not_ accessible (not running or network issue) then
            // channelAdapter will _not_ be initialised.
            // To test a lateinit (since Kotlin V 1.2) access the variable via its property
            // reference, and check the boolean property 'isInitialized'.
            // A property reference can be got at via the class of (::), the instance of, this
            // class.
            if(this::channelAdapter.isInitialized){
               channelAdapter.notifyDataSetChanged()
            }

            // channelAdapter.notifyDataSetChanged()
            //messageAdapter.notifyDataSetChanged()

            // Reset UI
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

            // Broadcast that we have logged out, to interested parties.
            // We do this AFTER we have cleared all the messages.
            val loggedOut = Intent(BROADCAST_LOGGED_OUT)
            LocalBroadcastManager.getInstance(this).sendBroadcast(loggedOut)

            // Leave the draw open.
        } else {
            // Login
            // Close the draw (navigation to another fragment, does not close the draw)!
            drawerLayout.closeDrawer(GravityCompat.START)
            // You can alternatively do: navigateToFragment(this, R.id.loginFragment)
            navigateToFragment(this,
                R.id.action_nav_main_to_loginFragment
            )
        }
    }

    fun sendMsgBtnClicked(view: View) {

        if(App.prefs.isLoggedIn && messageTextField.text.isNotEmpty() && MessageService.selectedChannel != null){
            val userId = UserDataService.id
            // The one rare example where you can use a !! operator since we know selectedChannel is
            // not null.
            val channelId = MessageService.selectedChannel!!.id
            // As before, be careful, the order of the parameters is important!
            // Also as previously noted this again is a potential security issue, if you have access
            // to a channel ID you may be able to send spurious messages with bogus user IDs (not
            // tested this out).
            socket.emit("newMessage", messageTextField.text.toString(), userId, channelId,
                UserDataService.name, UserDataService.avatarName, UserDataService.avatarColour)
            messageTextField.text.clear()
            hideKeyboard()
        }
    }

    private fun hideKeyboard(){
        // We need the input method service so we can manipulate the keyboard input system.
        // Deviation from course, 'currentFocus' is now a nullable.
        // BUG This is not working in the main activity, reason unknown.
        // Course fixes this with a manifest amend, adding property
        // 'android:windowSoftInputMode="stateAlwaysHidden"' to MainActivity application
        // definition.
        // This is now only called when the send message button click event, is processed.
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            // Flags: from research it looks like passing int 0 denotes force hide.
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}