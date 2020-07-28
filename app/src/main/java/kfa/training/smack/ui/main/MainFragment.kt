package kfa.training.smack.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kfa.training.smack.R
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Course deviation - this takes the place of the MainActivity for UI operations, in effect
 * this becomes "MainActivity".
 */


class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        // Remove live data part for the time being, will see if this can be made use of, later on
        // in the course.
//        val textView: TextView = root.findViewById(R.id.text_home)
//        mainViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        // BUG:
        // The test code below works!!
        // So what is the bug?
        // When logging out and back in in the _same_ session, non of the UI
        // elements in this fragment can be updated, __from MainActivity__.
        // It does work if the application is run up in the logged out state, then login is
        // performed.
        // It looks like the synthetics get messed up in some way when navigating away from this
        // fragment, suspect they are not re-attached to this fragments inflated root on
        // re-creation, but instead remain attached to the old fragment (which from the UI
        // perspective is now detached).
        // In mainActivity tried inflating fragment_main and directly accessing the mainChannelName
        // view (via findViewById()) to no avail!
        // It is strange however that this works on initial app run when logged out, since login
        // involves navigating away from fragment_main.
        // This is possibly why we have model data binders!
        val col = arrayOf(Color.BLUE, Color.GREEN, Color.RED)
        root.mainChannelName.setTextColor(col.random())

        return root
    }
}