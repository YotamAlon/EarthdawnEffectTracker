package io.github.yotamalon.earthdawneffecttracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.yotamalon.earthdawneffecttracker.R
import io.github.yotamalon.earthdawneffecttracker.databinding.ItemAggregatedEffectBinding
import io.github.yotamalon.earthdawneffecttracker.model.AggregatedEffect

class AggregatedEffectsAdapter :
    ListAdapter<AggregatedEffect, AggregatedEffectsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemAggregatedEffectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AggregatedEffect) {
            val context = binding.root.context
            binding.textTarget.text = item.target

            val formattedAmount = if (item.totalAmount > 0) "+${item.totalAmount}" else "${item.totalAmount}"
            binding.textTotalAmount.text = formattedAmount

            if (item.totalAmount > 0) {
                binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.positive_green))
            } else if (item.totalAmount < 0) {
                binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.negative_red))
            } else {
                binding.textTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.neutral_gray))
            }

            binding.textActiveCount.text = context.resources.getQuantityString(
                R.plurals.effects_active_count,
                item.activeCount,
                item.activeCount
            )
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
            override fun areItemsTheSame(
                oldItem: AggregatedEffect,
                newItem: AggregatedEffect
            ): Boolean {
                return oldItem.target == newItem.target
            }

            override fun areContentsTheSame(
                oldItem: AggregatedEffect,
                newItem: AggregatedEffect
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
