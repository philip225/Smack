package kfa.training.smack.Controller

import android.app.Application
import kfa.training.smack.utilities.SharedPrefs

class App: Application() {
    /**
     * Application holds the global state, by extending we can add our own preferences on
     * application creation.
     */

    companion object {
        lateinit var prefs: SharedPrefs
    }


    override fun onCreate() {
        /**
         * Called when the application is created but before any activity, service,
         * or receiver objects.
         * Initialise our shared preferences
         */
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}