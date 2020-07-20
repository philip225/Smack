package kfa.training.smack.ui.user

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kfa.training.smack.R
import kfa.training.smack.services.AuthService
import kfa.training.smack.utilities.toasty
import kotlinx.android.synthetic.main.fragment_create_user.*
import kotlin.random.Random

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

    var userAvatar = "profileDefault"
    // This is the format required for the back end DB.
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

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
        /**
         * Choose a random avatar.
         */
        // Kotlin now has Random mapped in, so you do not need an instantiated Random object any
        // more.
        val color = Random.nextInt(2)
        val avatar = Random.nextInt(28)
        userAvatar = if(color==0){
            "light$avatar"
        } else {
            "dark$avatar"
        }

        // Deviation from course, we do not have access to packageName directly inside a Fragment,
        // since we are not an Activity. However the main activity is exposed to this fragment via
        // 'activity' (getActivity()), we can use that to get our package name.
        val resourceId = resources.getIdentifier(userAvatar, "drawable",
            activity?.packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    private fun generateColourClicked(view: View){
        /**
         * Choose a random colour for the avatar
         */
        val r = Random.nextInt(255)
        val g = Random.nextInt(255)
        val b = Random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        // Course deviation, the conversion to a normalised float value of range [0,1], can be
        // done entirely in the template, saving on intermediate variables.
        avatarColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255}, 1]"
    }

    private fun createUserClicked(view: View){
        context?.let{
            AuthService.registerUser(
                it, "test@test.com","12345"
            ){
                context?.let{context ->
                    toasty(context, "Registered use response: $it")
                }
            }
        }
    }
}