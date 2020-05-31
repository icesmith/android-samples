package com.icesmith.retrofitsuspendingcalladapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.icesmith.retrofitsuspendingcalladapter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model
    }
}
