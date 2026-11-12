package com.example.earthdawneffecttracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.earthdawneffecttracker.R
import com.example.earthdawneffecttracker.databinding.ItemAggregatedEffectBinding
import com.example.earthdawneffecttracker.model.AggregatedEffect

class AggregatedEffectsAdapter :
    ListAdapter<AggregatedEffect, AggregatedEffectsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemAggregatedEffectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AggregatedEffect) {
            val context = binding.root.context
            binding.textTarget.text = item.target
            binding.textActiveCount.text = context.resources.getQuantityString(
                R.plurals.effects_active_count,
                item.activeCount,
                item.activeCount
            )

            val formattedAmount = if (item.totalAmount > 0) "+${item.totalAmount}" else "${item.totalAmount}"
            binding.textTotalAmount.text = formattedAmount

            when {
                item.totalAmount > 0 -> {
                    binding.textTotalAmount.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_positive)
                    binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.positive_green))
                }
                item.totalAmount < 0 -> {
                    binding.textTotalAmount.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_negative)
                    binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.negative_red))
                }
                else -> {
                    binding.textTotalAmount.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_neutral)
                    binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.neutral_gray))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAggregatedEffectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AggregatedEffect>() {
            override fun areItemsTheSame(oldItem: AggregatedEffect, newItem: AggregatedEffect): Boolean {
                return oldItem.target == newItem.target
            }

            override fun areContentsTheSame(oldItem: AggregatedEffect, newItem: AggregatedEffect): Boolean {
                return oldItem == newItem
            }
        }
    }
}
