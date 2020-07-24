package kfa.training.smack

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.adapters.TemporaryAdapter
import kfa.training.smack.services.AuthService
import kfa.training.smack.services.UserDataService
import kfa.training.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kfa.training.smack.utilities.navigateToFragment
import kfa.training.smack.utilities.toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    // private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    // Main activity will handle the recycler views as per the course.
    // These variables are temporary and will be updated in lesson 88. Download Channels.
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TemporaryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager // May not be used.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This is required to provide the draw pop-out toolbar button (three horizontal lines,
        // top left).
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        /* Floating action button is not used in this course.
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
         */
        // Notice drawerLayout is now global, this is needed else ware.
        drawerLayout  = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // We have no menu any more!! So this is not applicable.
//        appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        // The second parameter changes from appBarConfiguration to drawerLayout for the same
        // reason of their being no menu.
        setupActionBarWithNavController(navController, drawerLayout)
        navView.setupWithNavController(navController)

        /** Broadcast receiver - following the course and defining it here in the activity, instead
         * of in the main fragment **/
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))

        /**
         * Temporary recycler view setup to see it laid out at runtime in the draw, and to test the
         * recycler works correctly in the draw (including scrolling).
        **/
        val someItems    = mutableListOf<String>()
        for(ct in 1..20){
            someItems.add("Placeholder item $ct")
        }

        recyclerView = channel_list
        // Kotlin will smart cast someItems to immutable.
        viewAdapter = TemporaryAdapter(this, drawerLayout, someItems){
            Toast.makeText(this, "Draw item clicked! :: $it", Toast.LENGTH_LONG).show()
        }
        channel_list.adapter = viewAdapter
        // Remember the layout!
        channel_list.layoutManager = LinearLayoutManager(this)
        // Layout size per cell is not going to change, so we might as well optimise.
        channel_list.setHasFixedSize(true)
        /** END Temporary recycler view setup **/
    }

        // The course does not make use of an options menu.
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    private val userDataChangedReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            /**
             * This is called when we receive a broadcast, we will only receive a broadcast from
             * broadcasts sent inside our application for BROADCAST_USER_DATA_CHANGE.
             */
            if(AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName,
                    "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColour(UserDataService.avatarColour))
                loginBtnNavHeader.text = "Logout"
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        /**
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
       if(AuthService.isLoggedIn){
           val builder = AlertDialog.Builder(this)
           val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

           // Dialog note, when the positive and negative callbacks exit, the dialog is
           // hidden for us.

           builder.setView(dialogView)
               .setPositiveButton("Add"){ dialog: DialogInterface?, which: Int ->
                   // Course deviation, parameter 'i' is now named 'which'.
                   // Course deviation, findViewById is not required any more.
                   val nameTextField = dialogView.addChannelNameTxt
                   val descTextField = dialogView.addChannelDescTxt
                   val channelName = nameTextField.text.toString()
                   val channelDesc = descTextField.text.toString()

                   // Create channel with the channel name and description.
                   // This will be using web sockets, so we will have a continuous open socket
                   // (full duplex) communication.

               }
               .setNegativeButton("Cancel"){ dialog: DialogInterface?, which ->
                   // Course deviation, parameter 'i' is now named 'which'

               }.show()

       }
    }
    fun loginBtnNavClicked(view: View) {
        /**
         * Deviation from course, we are now using navigation to navigate to our login fragment.
         */

        if (AuthService.isLoggedIn){
            // Logout
            UserDataService.logout()

            // Reset UI
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

            // Leave the draw open.
        } else {
            // Login
            // Close the draw (navigation to another fragment, does not close the draw)!
            drawerLayout.closeDrawer(GravityCompat.START)
            // You can alternatively do: navigateToFragment(this, R.id.loginFragment)
            navigateToFragment(this, R.id.action_nav_main_to_loginFragment)
        }
    }

    fun sendMsgBtnClicked(view: View) {
        hideKeyboard()
        toasty(this, "Send message button clicked.")
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