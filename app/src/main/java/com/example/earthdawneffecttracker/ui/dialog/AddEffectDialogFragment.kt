package com.example.earthdawneffecttracker.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.earthdawneffecttracker.R
import com.example.earthdawneffecttracker.databinding.DialogAddEffectBinding
import com.example.earthdawneffecttracker.viewmodel.EffectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddEffectDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAddEffectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EffectViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEffectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTargetDropdown()
        setupQuickChips()

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonAdd.setOnClickListener {
            handleAddEffect()
        }
    }

    private fun setupTargetDropdown() {
        val targets = viewModel.presetTargets.value
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            targets
        )
        binding.inputTarget.setAdapter(adapter)
    }

    private fun setupQuickChips() {
        // Amount chips
        binding.chipAmountPlus1.setOnClickListener { binding.inputAmount.setText("1") }
        binding.chipAmountPlus2.setOnClickListener { binding.inputAmount.setText("2") }
        binding.chipAmountPlus3.setOnClickListener { binding.inputAmount.setText("3") }
        binding.chipAmountPlus5.setOnClickListener { binding.inputAmount.setText("5") }
        binding.chipAmountMinus1.setOnClickListener { binding.inputAmount.setText("-1") }
        binding.chipAmountMinus2.setOnClickListener { binding.inputAmount.setText("-2") }
        binding.chipAmountMinus3.setOnClickListener { binding.inputAmount.setText("-3") }

        // Rounds chips
        binding.chipRounds1.setOnClickListener { binding.inputRounds.setText("1") }
        binding.chipRounds2.setOnClickListener { binding.inputRounds.setText("2") }
        binding.chipRounds3.setOnClickListener { binding.inputRounds.setText("3") }
        binding.chipRounds5.setOnClickListener { binding.inputRounds.setText("5") }
    }

    private fun handleAddEffect() {
        val target = binding.inputTarget.text?.toString().orEmpty()
        val amountStr = binding.inputAmount.text?.toString().orEmpty()
        val roundsStr = binding.inputRounds.text?.toString().orEmpty()

        val amount = amountStr.toIntOrNull()
        val rounds = roundsStr.toIntOrNull()

        if (target.isBlank() || amount == null || amount == 0 || rounds == null || rounds <= 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_input_error),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val success = viewModel.addEffect(target, amount, rounds)
        if (success) {
            dismiss()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_input_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddEffectDialogFragment"
        fun newInstance() = AddEffectDialogFragment()
    }
}
