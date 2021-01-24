package com.icesmith.separatetoolbars

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.icesmith.separatetoolbars.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {
    lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        binding.toolbar.setupWithNavController(navController)

        // An example of creating a menu programmatically.
        // Take a look at SecondFragment.kt for an xml menu example.
        val settingsMenuItem = binding.toolbar.menu.add(R.string.first_fragment_menu_settings)
        settingsMenuItem.setOnMenuItemClickListener {
            Log.i("test", "settings")
            true
        }

        binding.toSecondButton.setOnClickListener {
            navController.navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }
}
