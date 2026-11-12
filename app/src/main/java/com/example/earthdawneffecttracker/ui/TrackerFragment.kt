package com.example.earthdawneffecttracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthdawneffecttracker.R
import com.example.earthdawneffecttracker.databinding.FragmentTrackerBinding
import com.example.earthdawneffecttracker.ui.adapter.ActiveEffectsAdapter
import com.example.earthdawneffecttracker.ui.adapter.AggregatedEffectsAdapter
import com.example.earthdawneffecttracker.ui.dialog.AddEffectDialogFragment
import com.example.earthdawneffecttracker.viewmodel.EffectViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrackerFragment : Fragment() {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EffectViewModel by activityViewModels()

    private lateinit var aggregatedAdapter: AggregatedEffectsAdapter
    private lateinit var activeAdapter: ActiveEffectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_reset -> {
                            confirmReset()
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    private fun setupRecyclerViews() {
        aggregatedAdapter = AggregatedEffectsAdapter()
        binding.recyclerAggregated.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aggregatedAdapter
        }

        activeAdapter = ActiveEffectsAdapter { effect ->
            viewModel.removeEffect(effect.id)
        }
        binding.recyclerActive.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddEffect.setOnClickListener {
            showAddEffectDialog()
        }

        binding.buttonFinishRound.setOnClickListener {
            val expiredCount = viewModel.finishRound()
            val msg = if (expiredCount > 0) {
                getString(R.string.effects_expired_msg, expiredCount)
            } else {
                getString(R.string.no_effects_expired_msg)
            }
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.aggregatedEffects.collectLatest { list ->
                aggregatedAdapter.submitList(list)
                if (list.isEmpty()) {
                    binding.textEmptyAggregated.visibility = View.VISIBLE
                    binding.recyclerAggregated.visibility = View.GONE
                } else {
                    binding.textEmptyAggregated.visibility = View.GONE
                    binding.recyclerAggregated.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.effects.collectLatest { list ->
                activeAdapter.submitList(list)
                if (list.isEmpty()) {
                    binding.textEmptyActive.visibility = View.VISIBLE
                    binding.recyclerActive.visibility = View.GONE
                } else {
                    binding.textEmptyActive.visibility = View.GONE
                    binding.recyclerActive.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showAddEffectDialog() {
        AddEffectDialogFragment.newInstance().show(
            childFragmentManager,
            AddEffectDialogFragment.TAG,
        )
    }

    private fun confirmReset() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reset_confirm_title)
            .setMessage(R.string.reset_confirm_msg)
            .setPositiveButton(R.string.reset_confirm_yes) { _, _ ->
                viewModel.resetAll()
                Snackbar.make(binding.root, "All effects reset", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.reset_confirm_no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
