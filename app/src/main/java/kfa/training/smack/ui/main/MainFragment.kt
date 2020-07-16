package kfa.training.smack.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kfa.training.smack.R

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
        return root
    }
}