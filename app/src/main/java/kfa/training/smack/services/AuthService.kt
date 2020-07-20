package kfa.training.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kfa.training.smack.utilities.URL_REGISTER
import org.json.JSONObject
import java.nio.charset.Charset

// Defined as a singleton object, since we do not want >1 instantiated AuthService.
object AuthService {

    fun registerUser(context: Context, email: String, password:String, complete:(Boolean) -> Unit){
        /**
         * Register a user.
         */
        val jsonBody = JSONObject()
        jsonBody.put(
            "email", email
        )
        jsonBody.put(
            "password", password
        )
        val requestBody = jsonBody.toString()

        // This part seems "messy" where it is and is really begging to be moved into a private
        // function.
        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener {
            // This is called back on a successful request.
            complete(true)
        }, Response.ErrorListener {
            Log.d("AUTH/ERROR", "Could not register user $it")
            complete(false)
        }){
            // We are an anonymous class of type StringRequest which gives us certain methods we
            // need to override.

            override fun getBodyContentType(): String {
                // This allows for the full HTTP content type where you can specify the mime and
                // the character set.
                // JSON character set is always UTF8, we have to specify this for Volley otherwise
                // the arcane ISO-8859-1 character set will be used (since that is the default for
                // HTTP protocol)!
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                // Send our body, which we have to convert to a byte array encoded to UTF8.
                // Course deviation, I formally encode the byte array to UTF8, the course
                // relies on the default.
                return requestBody.toByteArray(Charset.forName("utf-8"))
            }
        }

        // Add our request to a new request queue
        Volley.newRequestQueue(context).add(registerRequest)
    }
}