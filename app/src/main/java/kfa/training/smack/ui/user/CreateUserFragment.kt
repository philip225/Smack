package kfa.training.smack.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kfa.training.smack.R
import kfa.training.smack.utilities.toasty
import kotlinx.android.synthetic.main.fragment_create_user.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * This replaces the create user activity, in the original course.
 * This has no associated view model.
 */
class CreateUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Wire up our click handlers
        createAvatarImageView.setOnClickListener {
            generateUserAvatar(it)
        }
        backgroundColourBtn.setOnClickListener {
            generateColourClicked(it)
        }
        createUserBtn.setOnClickListener {
            createUserClicked(it)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun generateUserAvatar(view: View){
        context?.let{
            toasty(it, "Generate user avatar clicked.")
        }
    }

    private fun generateColourClicked(view: View){
        context?.let{
            toasty(it, "Generate colour clicked.")
        }
    }

    private fun createUserClicked(view: View){
        context?.let{
            toasty(it, "Create user clicked.")
        }
    }
}