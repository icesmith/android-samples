package com.icesmith.recyclerviewcontainertransition

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.icesmith.recyclerviewcontainertransition.databinding.FooListItemBinding
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Foo(
    val id: Int,
    val text: String
) : Parcelable

object FooDiffCallback : DiffUtil.ItemCallback<Foo>() {
    override fun areItemsTheSame(oldItem: Foo, newItem: Foo) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Foo, newItem: Foo) = oldItem == newItem
}

class FooAdapter(
    private val itemListener: FooListener
) : ListAdapter<Foo, FooViewHolder>(FooDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FooListItemBinding.inflate(inflater, parent, false)
        return FooViewHolder(binding, itemListener)
    }

    override fun onBindViewHolder(holder: FooViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

interface FooListener {
    fun onClick(item: Foo, itemView: View)
}

class FooViewHolder(
    private val binding: FooListItemBinding,
    listener: FooListener
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.listener = listener
    }

    fun bind(foo: Foo) {
        binding.foo = foo
        binding.executePendingBindings()
    }
}
