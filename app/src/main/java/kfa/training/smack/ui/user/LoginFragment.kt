package kfa.training.smack.ui.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kfa.training.smack.R
import kfa.training.smack.services.AuthService
import kfa.training.smack.utilities.navigateToFragment
import kfa.training.smack.utilities.toasty
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Course deviation, in the original course this was an activity, now converted to a
 * navigable fragment.
 */

class LoginFragment : Fragment() {
    private lateinit var drawerLayout: DrawerLayout

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

        // Hide our spinner
        loginSpinner.visibility = View.INVISIBLE

        loginLoginBtn.setOnClickListener{
            // Call our callbacks
            loginLoginBtnClicked(it)
        }
        loginCreateUserBtn.setOnClickListener {
            loginCreateUserBtnClicked(it)
        }

        activity?.let{
            drawerLayout  = it.findViewById(R.id.drawer_layout)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loginLoginBtnClicked(view: View) {
        enableSpinner(true)
        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        hideKeyboard()

        if(email.isEmpty() || password.isEmpty()){
            toasty(context, "Please fill in both email and password.")
            return
        }


        context?.let{
            AuthService.loginUser(email, password){ loginSuccess ->
                if(loginSuccess){
                    AuthService.findUserByEmail(it){findSuccess ->
                        if (findSuccess){
                            enableSpinner(false)
                            // Deviation from course:
                            // Draw needs to be open for when we land back in the main fragment.
                            drawerLayout.openDrawer(GravityCompat.START)

                            // Deviation from course, we navigate back to the main fragment
                            // via the id action_loginFragment_to_nav_main
                            navigateToFragment(this,
                                R.id.action_loginFragment_to_nav_main
                            )
                        } else {
                            // Find user by email failed
                            errorToast()
                        }
                    }
                } else {
                    // Login failed
                    errorToast()
                }

            }
        }

    }
    private fun loginCreateUserBtnClicked(view: View) {
        /**
         * Navigate to the create user fragment
         */
        navigateToFragment(this,
            R.id.action_loginFragment_to_createUserFragment
        )
    }

    private fun errorToast() {
        // Deviation from course, made use of a previous util function, created for this re-write,
        // to reduce Toast code.
        toasty(context, "Something went wrong, please try again.")
        enableSpinner(false)
    }

    private fun enableSpinner(enable: Boolean){
        /**
         * Show or hide the spinner and disable or enable the buttons.
         */

        // Course deviation simplified the code further by turning the if into an expression
        // Also presented here is an oddity, no {}!
        // Single lines thus don't need them!
        loginSpinner.visibility = if(enable)
            View.VISIBLE
        else
            View.INVISIBLE

        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }

    private fun hideKeyboard(){
        // We need the input method service so we can manipulate the keyboard input system.
        // Deviation from course, 'activity' and 'currentFocus' are both nullables inside a
        // fragment.
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}