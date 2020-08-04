package kfa.training.smack.utilities

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kfa.training.smack.R

infix fun Activity.navigateToFragment(navigableResourceID:Int){
    /**
     * Convenience infix to shorten the code for navigation, from an activity.
     * @param navigableResourceID: A navigation path resource or navigation fragment id.
     */
    // This is subtly different to the normal call of Activity.findNavController(), since
    // we are navigating from an Activity to a Fragment and need to specify
    findNavController(this, R.id.nav_host_fragment).navigate(navigableResourceID)
}

infix fun Fragment.navigateToFragment(navigableResourceID:Int){
    /**
     * Convenience infix to shorten the code for navigation, from a fragment.
     * @param navigableResourceID: A navigation path resource or navigation fragment id.
     */
    this.findNavController().navigate(navigableResourceID)
}