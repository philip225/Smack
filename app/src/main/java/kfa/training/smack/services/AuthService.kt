package kfa.training.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kfa.training.smack.utilities.*
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

// Defined as a singleton object, since we do not want >1 instantiated AuthService.
object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

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

    fun loginUser(context: Context, email: String, password:String, complete:(Boolean) -> Unit){
        /**
         * Login a user
         */
        // We have duplication here - suggesting some code can be encapsulated into a "send"
        // function
        val jsonBody = JSONObject()
        jsonBody.put(
            "email", email
        )
        jsonBody.put(
            "password", password
        )
        val requestBody = jsonBody.toString()
        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->
            // 'response' is a JSON object.
            // The email is in the 'user' field NOT the 'email' field!

            try{
                userEmail = response.getString("user")
                authToken = response.getString("token")
                isLoggedIn = true
                complete(true)
            } catch (e: JSONException){
                // This suggests the back end API has changed!
                // Deviation from course, do not need to concat e.localizedMessage, use string
                // templating instead!
                Log.d("AUTH/ERROR",
                    "Issue with JSON response for login: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {error ->
            // Error with login request.
            Log.d("AUTH/ERROR", "Issue with login request: $error")
            complete(false)
        }) {
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

        // Add to a new request queue.
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String,
                   avatarColour: String, complete: (Boolean) -> Unit){
        /**
         * Create the user.
         */
        // You would think registerUser could perform this and save on this function, but think!
        // This is done separately to reduce the possibility of overwriting and existing users
        // details.
        val jsonBody = JSONObject()
        jsonBody.put(
            "name", name
        )
        jsonBody.put(
            "email", email
        )
        jsonBody.put(
            "avatarName", avatarName
        )
        jsonBody.put(
            "avatarColor", avatarColour
        )

        val requestBody = jsonBody.toString()

        val createRequest = object: JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {response ->
            try{
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColour = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                complete(true)
            } catch(e: JSONException){
                Log.d("AUTH/ERROR", "Issue with create user JSON: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("AUTH/ERROR", "Issue with create user request: $error")
            complete(false)
        }){
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

            override fun getHeaders(): MutableMap<String, String> {
                /*
                This is a restricted call which requires authentication so we need to provide
                the basic auth header.
                 */

                // Deviation from course, using mutableMapOf and 'to' operator you can reduce the
                // code to this:
                return mutableMapOf(
                    "Authorization" to "Bearer $authToken"
                )
            }

        }

        // Add to a new request queue.
        Volley.newRequestQueue(context).add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){
        /**
         * Find the user by the given email.
         */
        val findUserRequest = object: JsonObjectRequest(Method.GET, "$URL_GET_USER$userEmail", null,
            Response.Listener {response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColour = response.getString("avatarColor")
                    // Recall this is _id not id!
                    UserDataService.id = response.getString("_id")

                    // We have changed user data in UserDataService, so we broadcast this fact.
                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                    complete(true)
                } catch (e: JSONException){
                    Log.d("AUTH/ERROR", "User login error, corrupt JSON response: $e")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("AUTH/ERROR", "Could not log in user due to $error")
                complete(false)
            }){

            override fun getHeaders(): MutableMap<String, String> {
                /*
                This is a restricted call which requires authentication so we need to provide
                the basic auth header.
                 */

                // Deviation from course, using mutableMapOf and 'to' operator you can reduce the
                // code to this:
                return mutableMapOf(
                    "Authorization" to "Bearer $authToken"
                )
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)
    }


}