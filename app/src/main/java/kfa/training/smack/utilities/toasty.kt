package kfa.training.smack.utilities

import android.content.Context
import android.widget.Toast

fun toasty(context: Context?, message: String){
    /**
     * Convenience function interface to Toast.
     * Context is set nullable so you can pass 'context' from inside a fragment, as is.
     */
    context?.let{
        Toast.makeText(it,message,Toast.LENGTH_LONG).show()
    }
}