package com.icesmith.androidsamples

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

data class User(
    val id: String,
    val firstName: String,
    val lastName: String
)

class MainVM : ViewModel() {
    val userId = object: MutableLiveData<String>() {
        override fun setValue(value: String) {
            if (value != this.value) {
                super.setValue(value)
            }
        }
    }

    val user = userId.switchMap { userId -> liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        delay(2000)
        emit(User(userId, "Ice", "Smith"))
    }}

    fun saveUser(firstName: CharSequence, lastName: CharSequence) {
        Log.i(this.javaClass.name, "saveUser: $firstName $lastName")
    }
}
