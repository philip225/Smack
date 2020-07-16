package kfa.training.smack.utilities

import android.app.Activity
import androidx.navigation.Navigation.findNavController
import kfa.training.smack.R
import kotlinx.android.synthetic.main.content_main.*

fun navigateToFragment(activity:Activity, navigableResourceID:Int){
    val navController = findNavController(activity, R.id.nav_host_fragment)
    navController.navigate(navigableResourceID)
}