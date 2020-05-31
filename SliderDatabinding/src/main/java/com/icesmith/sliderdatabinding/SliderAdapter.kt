package com.icesmith.sliderdatabinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.slider.Slider

@BindingAdapter("sliderValue")
fun setSliderValue(slider: Slider, value: Float) {
    slider.value = value
}

@InverseBindingAdapter(attribute = "sliderValue")
fun getSliderValue(slider: Slider) = slider.value

@BindingAdapter( "sliderValueAttrChanged")
fun setSliderListeners(slider: Slider, attrChange: InverseBindingListener) {
    slider.addOnChangeListener { _, _, _ ->
        attrChange.onChange()
    }
}
