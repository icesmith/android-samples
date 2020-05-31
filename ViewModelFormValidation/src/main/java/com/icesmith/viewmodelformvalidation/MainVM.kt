package com.icesmith.viewmodelformvalidation

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
    private val userId = object: MutableLiveData<String>("my-user") {
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
