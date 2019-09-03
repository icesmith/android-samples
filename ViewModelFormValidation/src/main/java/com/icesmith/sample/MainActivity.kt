package com.icesmith.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.icesmith.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var model: MainVM
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProviders.of(this)[MainVM::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model.apply {
            userId.value = "my-user"
        }
    }

    fun onSaveButtonClick(@Suppress("UNUSED_PARAMETER") button: View) {
        val firstName = binding.firstNameInput.text.trim()
        if (firstName.isBlank()) {
            binding.firstNameInput.error = getString(R.string.first_name_is_blank)
            return
        }

        val lastName = binding.lastNameInput.text.trim()
        if (lastName.isBlank()) {
            binding.lastNameInput.error = getString(R.string.last_name_is_blank)
            return
        }

        model.saveUser(firstName, lastName)
    }
}
