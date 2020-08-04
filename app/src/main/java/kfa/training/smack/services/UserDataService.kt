package kfa.training.smack.services

import android.graphics.Color
import android.util.Log
import kfa.training.smack.Controller.App
import org.json.JSONArray
import org.json.JSONException

object UserDataService {

    var id = ""
    var avatarColour = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout(){
        id = ""
        avatarColour = ""
        avatarName = ""
        email = ""
        name = ""
        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }


    fun returnAvatarColour(components: String): Int{
        /**
         * Provide the colour RGB integer for the given component string
         */
        // Interesting, the course uses text parsing instead of JSON decoding, which is
        // not as robust.
        // Deviation from course, from programming experience, chosen to use JSON decoding for
        // robustness.
        // Also this could be re-factored, since we have access to avatarColour there is no need
        // for the 'components' parameter.
        val r: Int
        val g: Int
        val b: Int
        try{
            val reader = JSONArray(components)
            r = ((reader[0] as Double) * 255).toInt()
            g = ((reader[1] as Double) * 255).toInt()
            b = ((reader[2] as Double) * 255).toInt()
        } catch (e: JSONException){
            Log.d("AVATAR/COL", "Bad JSON returned: $e")
            return Color.rgb(0,0,0)
        }
        return Color.rgb(r,g,b)
    }
}