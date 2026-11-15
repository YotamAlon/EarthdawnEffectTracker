package com.example.earthdawneffecttracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.earthdawneffecttracker.databinding.ItemActiveConditionBinding
import com.example.earthdawneffecttracker.model.Condition

class ActiveConditionsAdapter(
    private val onStackChange: (Condition, Int) -> Unit,
    private val onDeleteClick: (Condition) -> Unit
) : ListAdapter<Condition, ActiveConditionsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: ItemActiveConditionBinding,
        private val onStackChange: (Condition, Int) -> Unit,
        private val onDeleteClick: (Condition) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Condition) {
            binding.textConditionName.text = item.name
            binding.textStacks.text = item.stacks.toString()

            binding.buttonMinus.setOnClickListener {
                onStackChange(item, -1)
            }
            binding.buttonPlus.setOnClickListener {
                onStackChange(item, 1)
            }
            binding.buttonDeleteCondition.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActiveConditionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onStackChange, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Condition>() {
            override fun areItemsTheSame(oldItem: Condition, newItem: Condition): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Condition, newItem: Condition): Boolean {
                return oldItem == newItem
            }
        }
    }
}
