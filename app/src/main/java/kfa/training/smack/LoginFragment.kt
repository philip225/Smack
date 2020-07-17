package kfa.training.smack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kfa.training.smack.utilities.navigateToFragment
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Course deviation, in the original course this was an activity, now converted to a
 * navigable fragment.
 */


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var drawerLayout: DrawerLayout

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /**
         * onClick handler defined in the layout XML is not usable in fragments, for the
         * reasons explained in fragment_login.xml
         * So here we manually wire up our on click listeners, to the login and user creation
         * buttons:
         * Also, we have to define this here since the view has to have been layed out.
         */
        loginLoginBtn.setOnClickListener{
            // Call our callbacks
            loginLoginBtnClicked(it)
        }
        loginCreateUserBtn.setOnClickListener {
            loginCreateUserBtnClicked(it)
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loginLoginBtnClicked(view: View) {
        Toast.makeText(context, "LoginFragment: Login button clicked!", Toast.LENGTH_LONG).show()

    }
    private fun loginCreateUserBtnClicked(view: View) {
        /**
         * Navigate to the create user fragment
         */
        navigateToFragment(this, R.id.action_loginFragment_to_createUserFragment)
    }
}