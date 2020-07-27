package kfa.training.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import kfa.training.smack.Controller.App
import kfa.training.smack.Model.Channel
import kfa.training.smack.Model.Message
import kfa.training.smack.utilities.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {
    /**
     * Handles the storing and processing of channels.
     */
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){
        val channelsRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            /** Data returned **/
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
                // Course deviation, used hashMapOf with 'to' operator to save on lines.
                val headers = hashMapOf(
                    "Authorization" to "Bearer ${App.prefs.authToken}"
                )
                return headers
            }
        }
        // Volley request!
        App.prefs.requestQueue.add(channelsRequest)
    }
}