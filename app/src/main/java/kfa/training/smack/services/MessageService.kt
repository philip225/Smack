package kfa.training.smack.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import kfa.training.smack.Controller.App
import kfa.training.smack.Model.Channel
import kfa.training.smack.Model.Message
import kfa.training.smack.utilities.URL_GET_CHANNELS
import kfa.training.smack.utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {
    /**
     * Handles the storing and processing of channels.
     */
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    // Deviation from course, we need to communicate across the application which channel is
    // currently selected.
    var selectedChannel: Channel? = null

    fun getChannels(complete: (Boolean) -> Unit){
        val channelsRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            /** Data returned **/
            // Clear existing channels
            clearChannels()
            // Decode the JSON
            try{
                // This seems long winded, since we requested a JSON array, would have expected
                // response to be an iterator, allowing iteration over each JSON object.
                // Library does not support that, todo: Infix time!
                for(x in 0 until response.length()){
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val chanDesc = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val newChannel = Channel(name, chanDesc, channelId)

                    // Course deviation, 'this' is not required.
                    channels.add(newChannel)
                }
                complete(true)
            } catch(e: JSONException){
                Log.d("MS/ERROR", "JSON decode error when decoding fetched channels: $e")
                complete(false)
            }

        }, Response.ErrorListener {error ->
            /** Error **/
            Log.d("MS/ERROR", "Could not retreave channels due to $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                // Course deviation, used hashMapOf with 'to' operator and inlined, to save on
                // lines.
                return hashMapOf(
                    "Authorization" to "Bearer ${App.prefs.authToken}"
                )
            }
        }
        // Volley request!
        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId:String, complete: (Boolean) -> Unit){
        clearMessages()
        val url = "$URL_GET_MESSAGES$channelId"
        val messageRequest = object: JsonArrayRequest(Method.GET, url, null, Response.Listener {response ->
            // OK - handle the response - decode JSON
            try{
                for(x in 0 until response.length()){
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColour = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(
                        messageBody, userName, channelId, userAvatar, userAvatarColour, id,
                        timeStamp
                    )
                    // Course deviation: 'this' is optional.
                    // Course deviation: only add the new message if it does not exist, this
                    // is a guard for us being called, multiple times.
                    if(newMessage !in messages) messages.add(newMessage)
                }
                complete(true)
            } catch(e: JSONException){
                Log.d("MS/ERROR", "JSON error when decoding channel list: $e")
                complete(false)
            }
            complete(true)
        }, Response.ErrorListener {error ->
            // Error
            Log.d("MS/ERROR","Error when getting messages: $error")
            complete(false)
        }){
            // Setup headers e.t.c
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                // Course deviation, used hashMapOf with 'to' operator and inlined, to save on
                // lines.
                return hashMapOf(
                    "Authorization" to "Bearer ${App.prefs.authToken}"
                )
            }
        }
        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages(){
        messages.clear()
    }

    fun clearChannels(){
        channels.clear()
    }
}