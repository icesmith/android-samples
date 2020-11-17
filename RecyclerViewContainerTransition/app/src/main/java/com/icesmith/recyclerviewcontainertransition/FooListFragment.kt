package com.icesmith.recyclerviewcontainertransition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.icesmith.recyclerviewcontainertransition.databinding.FragmentFooListBinding

class FooListFragment : Fragment() {
    private lateinit var binding: FragmentFooListBinding

    private val itemListener = object : FooListener {
        override fun onClick(item: Foo, itemView: View) {
            val transitionName = getString(R.string.foo_details_transition_name)
            val extras = FragmentNavigatorExtras(itemView to transitionName)

            val directions = FooListFragmentDirections.actionToFooDetailsFragment(item)
            val navController = findNavController()
            navController.navigate(directions, extras)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFooListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Postpone enter transitions to allow shared element transitions to run.
        // https://github.com/googlesamples/android-architecture-components/issues/495
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val items = List(20) { i -> Foo(i, "item $i") }
        binding.recyclerView.adapter = FooAdapter(itemListener).apply {
            submitList(items)
        }
    }
}
