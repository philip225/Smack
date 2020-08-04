package kfa.training.smack.ui.user

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kfa.training.smack.R
import kfa.training.smack.services.AuthService
import kfa.training.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kfa.training.smack.utilities.navigateToFragment
import kfa.training.smack.utilities.toasty
import kotlinx.android.synthetic.main.fragment_create_user.*
import kotlin.random.Random

/**
 * This replaces the create user activity, in the original course.
 * This has no associated view model.
 */
class CreateUserFragment : Fragment() {

    private var userAvatar = "profileDefault"
    // This is the format required for the back end DB.
    private var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * For any fragment any manipulation of the View objects has to be done in onViewCreated,
         * since they do not exist until the fragment view has been created.
         * You can access any of the view objects via synthetic imports, before onViewCreated is
         * called, but they will all be set to null!
         */

        // Wire up our click handlers
        createAvatarImageView.setOnClickListener {
            generateUserAvatar()
        }
        backgroundColourBtn.setOnClickListener {
            generateColourClicked()
        }
        createUserBtn.setOnClickListener {
            createUserClicked()
        }

        // Hide our spinner
        createSpinner.visibility = View.INVISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

    private fun generateUserAvatar(){
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

    private fun generateColourClicked(){
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

    private fun createUserClicked(){
        enableSpinner(true)
        val userName = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()
        /*
        We are in a fragment so we are not a context! We need to get the activity
        context, in our case the MainActivity context.
        This is provided for us via getContext() which is Kotlin mapped to 'context'.
        Note that the context can be null if we are a detached fragment, thus 'context' is a
        nullable.
         */

        // Included code from a course commenter (name redacted for GDPR), who commented on their
        // being too many nested ifs!
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            toasty(context, "Make sure username, email and password are filled in.")
            enableSpinner(false)
            return
        }

        context?.let{context ->
            AuthService.registerUser(
                email,password
            ){registerSuccess ->
                if(registerSuccess){
                    AuthService.loginUser(email, password){ loginSuccess ->
                        if(loginSuccess){
                            AuthService.createUser(
                                userName, email,userAvatar, avatarColor
                            ){createSuccess ->
                                if(createSuccess){
                                    /*
                                    Done !
                                    Deviation from the course, we are navigating back to
                                    the previous fragment, we do not have access to finish() (we
                                    are a fragment not an activity).
                                    To emulate finish() we can go back one step, technically this
                                    is wrong, we should navigate to our main fragment since we are
                                    logged in.
                                    I presume the course deals with this in the next lecture
                                    (81. Polishing up Create User Activity).
                                    I would have done this:
                                    navigateToFragment(this,
                                        R.id.action_createUserFragment_to_nav_main)
                                    Note that this path has to be added in the Studio navigation
                                    resource manager, it has been for this commit.
                                    */
                                    // Disable the spinner (not sure why we need to do this since
                                    // we are exiting this fragment).
                                    enableSpinner(false)

                                    /*
                                    Communication back to the home fragment (main activity in
                                    the course), in this course broadcasts are used.
                                    https://developer.android.com/guide/components/broadcasts
                                    You can alternatively send data to fragments since fragments
                                    support parameterised data, being passed via bundles.
                                    Thus you could pass data via navigation to the home fragment
                                    which has the token and other user detains in it.

                                    Broadcasts are intents which are broadcast (instead of being
                                    run) by the broadcast system.
                                    Typical call (inside fragment): context?.sendBroadcast(intent)
                                    However this is insecure, Google recommends that
                                    broadcasts, who's audience is just this app, make use of
                                    local broadcasts, via a LocalBroadcastManager.
                                    These are secure (only your app receives them) and as a bonus
                                    are more efficient, since they do not use IPC.
                                    */
                                    val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(
                                        userDataChanged)

                                    // Deviation from course, navigation works back to main via
                                    // the login activity. With fragment navigation that makes no
                                    // sense any more, when we can directly navigate back to the
                                    // main fragment.
                                    // Looking at our navigation we want to navigate via the
                                    // resource action_createUserFragment_to_nav_main
                                    this.navigateToFragment(R.id.action_createUserFragment_to_nav_main)

                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        }
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
        createSpinner.visibility = if(enable)
            View.VISIBLE
         else
            View.INVISIBLE

        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        backgroundColourBtn.isEnabled = !enable
    }


}