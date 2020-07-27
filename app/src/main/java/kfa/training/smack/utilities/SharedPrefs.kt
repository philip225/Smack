package kfa.training.smack.utilities

import android.content.Context
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {
    /**
     * Shared preferences access class, provides convenience functionality to save/load
     * specific shared preferences, like login details.
     */
    // Minor course deviation, use the official designated mode constant, instead of 0.
    // Where possible you should use official API constants sincce these values may change between
    // API releases.
    val prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        // Deviation from course, constants specific to a class should be defined in a companion
        // object.
        // Since this is a companion object (a singleton) we will have one and only one constant
        // defined, for the life of this class object.
        const val PREFS_FILE_NAME = "prefs"
        const val IS_LOGGED_IN = "isLoggedIn"
        const val AUTH_TOKEN = "authToken"
        const val USER_EMAIL = "userEmail"
    }
    // First time in this course were we have a case to use a custom getter and setter!
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    // Deviation from course, SharedPreferences.getString now returns a nullable so an elvis
    // operator (?:) has been used to satisfy this case (even though we return a default!).
    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    // Deviation from course, applied the same elvis operator for the same reasons it was used to
    // get the auth token.
    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    // We only need one request queue for the whole application, so we can
    // define it here.
    var requestQueue = Volley.newRequestQueue(context)
}