package com.icesmith.sample.sliderdatabinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainVM : ViewModel() {
    val count = MutableLiveData(10.0f)
}
