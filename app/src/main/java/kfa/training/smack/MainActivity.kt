package kfa.training.smack

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kfa.training.smack.adapters.TemporaryAdapter
import kotlinx.android.synthetic.main.activity_main.*

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

        /**
         * Temporary recycler view setup to see it laid out at runtime in the draw, and to test the
         * recycler works correctly in the draw (including scrolling).
        **/
        val someItems = mutableListOf<String>()
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
        Toast.makeText(this, "Add channel button clicked.", Toast.LENGTH_LONG).show()
    }
    fun loginBtnNavClicked(view: View) {
        Toast.makeText(this, "Login button clicked.", Toast.LENGTH_LONG).show()
    }

    fun sendMsgBtnClicked(view: View) {
        Toast.makeText(this, "Send message button clicked.", Toast.LENGTH_LONG).show()
    }
}