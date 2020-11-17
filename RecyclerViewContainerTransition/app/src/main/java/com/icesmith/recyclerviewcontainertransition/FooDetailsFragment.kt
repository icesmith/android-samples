package com.icesmith.recyclerviewcontainertransition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.icesmith.recyclerviewcontainertransition.databinding.FragmentFooDetailsBinding

class FooDetailsFragment : Fragment() {
    private val args: FooDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentFooDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 1000
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFooDetailsBinding.inflate(inflater, container, false)
        binding.foo = args.foo
        return binding.root
    }
}
