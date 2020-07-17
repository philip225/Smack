package kfa.training.smack.utilities

import android.content.Context
import android.widget.Toast

fun toasty(context: Context, message: String){
    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
}