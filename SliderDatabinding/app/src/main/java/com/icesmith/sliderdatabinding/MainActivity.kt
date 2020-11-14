package com.icesmith.sliderdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.icesmith.sliderdatabinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainVM>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.model = model

        model.count.observe(this, Observer { count ->
            Log.i(javaClass.name, "" + count)
        })
    }
}
