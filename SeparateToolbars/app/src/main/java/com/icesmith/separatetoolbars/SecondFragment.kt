package com.icesmith.separatetoolbars

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.icesmith.separatetoolbars.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)

        // This fragment uses a menu defined in xml file. A menu can be created
        // programmatically as well, see FirstFragment.kt for an example.
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                // these ids should match item ids from second_fragment_menu.xml file
                R.id.edit -> Log.i("test", "edit")
                R.id.share -> Log.i("test", "share")
            }

            // by returning 'true' we're saying that the event
            // is handled and it shouldn't be propagated further
            true
        }
    }
}
