package kfa.training.smack.utilities

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kfa.training.smack.R

fun navigateToFragment(activity:Activity, navigableResourceID:Int){
    /**
     * Convenience function to shorten the code for navigation, from an activity.
     * @param activity: Main activity, if this is called from inside an Activity then pass 'this'.
     * @param navigableResourceID: A navigation path resource or navigation fragment id.
     */
    // This is subtly different to the normal call of Activity.findNavController(), since
    // we are navigating from an Activity to a Fragment and need to specify
    findNavController(activity, R.id.nav_host_fragment).navigate(navigableResourceID)
}

// For the curious this is an example of function overloading.
// See https://kotlinlang.org/docs/tutorials/kotlin-for-py/functions.html#overloading
fun navigateToFragment(fragment: Fragment, navigableResourceID:Int){
    /**
     * Convenience function to shorten the code for navigation, from a fragment.
     * @param fragment: Fragment, if this is called from inside a Fragment then pass 'this'.
     * @param navigableResourceID: A navigation path resource or navigation fragment id.
     */
    fragment.findNavController().navigate(navigableResourceID)
}