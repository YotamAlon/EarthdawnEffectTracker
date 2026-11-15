package com.example.earthdawneffecttracker.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.earthdawneffecttracker.R
import com.example.earthdawneffecttracker.databinding.DialogAddConditionBinding
import com.example.earthdawneffecttracker.viewmodel.EffectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class AddConditionDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAddConditionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EffectViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPresetChips()

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonAddCustom.setOnClickListener {
            handleAddCustomCondition()
        }
    }

    private fun setupPresetChips() {
        val presetConditions = viewModel.presetConditions.value
        binding.chipGroupPresetConditions.removeAllViews()

        for (conditionName in presetConditions) {
            val chip = Chip(requireContext()).apply {
                text = conditionName
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    viewModel.addCondition(conditionName)
                    dismiss()
                }
            }
            binding.chipGroupPresetConditions.addView(chip)
        }
    }

    private fun handleAddCustomCondition() {
        val customName = binding.inputCustomCondition.text?.toString().orEmpty()
        if (customName.isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_condition_error),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val success = viewModel.addCondition(customName)
        if (success) {
            dismiss()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_condition_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddConditionDialogFragment"
        fun newInstance() = AddConditionDialogFragment()
    }
}
