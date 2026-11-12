package com.example.earthdawneffecttracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.earthdawneffecttracker.R
import com.example.earthdawneffecttracker.databinding.ItemActiveEffectBinding
import com.example.earthdawneffecttracker.model.Effect

class ActiveEffectsAdapter(
    private val onDeleteClick: (Effect) -> Unit
) : ListAdapter<Effect, ActiveEffectsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: ItemActiveEffectBinding,
        private val onDeleteClick: (Effect) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Effect) {
            val context = binding.root.context
            binding.textTarget.text = item.target

            val formattedAmount = if (item.amount > 0) "+${item.amount}" else "${item.amount}"
            binding.textAmount.text = formattedAmount

            if (item.amount > 0) {
                binding.textAmount.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_positive)
                binding.textAmount.setTextColor(ContextCompat.getColor(context, R.color.positive_green))
            } else {
                binding.textAmount.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_negative)
                binding.textAmount.setTextColor(ContextCompat.getColor(context, R.color.negative_red))
            }

            binding.textRounds.text = context.resources.getQuantityString(
                R.plurals.rounds_remaining_count,
                item.roundsRemaining,
                item.roundsRemaining
            )

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActiveEffectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Effect>() {
            override fun areItemsTheSame(oldItem: Effect, newItem: Effect): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Effect, newItem: Effect): Boolean {
                return oldItem == newItem
            }
        }
    }
}
