package com.icesmith.viewmodelformvalidation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.icesmith.viewmodelformvalidation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainVM>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model
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
