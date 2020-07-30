package kfa.training.smack.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

/* It looks like this is going to come into play! */
class MainViewModel : ViewModel() {

    val mainChannelName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val listViewScroll: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}