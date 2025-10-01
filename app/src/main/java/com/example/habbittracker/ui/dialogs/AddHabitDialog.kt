package com.example.habbittracker.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.habbittracker.R
import com.example.habbittracker.data.Habit
import com.example.habbittracker.databinding.DialogAddHabitBinding

/**
 * Dialog for adding or editing habits
 */
class AddHabitDialog(
    private val habit: Habit? = null,
    private val onHabitAdded: (name: String, type: String, targetCount: Int) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogAddHabitBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddHabitBinding.inflate(layoutInflater)
        
        setupUI()
        
        return androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(if (habit != null) "Edit Habit" else "Add Habit")
            .setPositiveButton("Save") { _, _ -> saveHabit() }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupUI() {
        // Setup type spinner
        val types = listOf("single", "countable")
        val typeLabels = listOf("Single", "Countable")
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
        
        // Setup target count visibility
        binding.spinnerType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isCountable = types[position] == "countable"
                binding.layoutTargetCount.visibility = if (isCountable) android.view.View.VISIBLE else android.view.View.GONE
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Populate fields if editing
        habit?.let { existingHabit ->
            binding.editTextName.setText(existingHabit.name)
            
            val typeIndex = types.indexOf(existingHabit.type)
            if (typeIndex != -1) {
                binding.spinnerType.setSelection(typeIndex)
            }
            
            binding.editTextTargetCount.setText(existingHabit.targetCount.toString())
        }
    }
    
    private fun saveHabit() {
        val name = binding.editTextName.text.toString().trim()
        val typeIndex = binding.spinnerType.selectedItemPosition
        val type = if (typeIndex == 0) "single" else "countable"
        
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
            return
        }
        
        val targetCount = if (type == "countable") {
            val targetText = binding.editTextTargetCount.text.toString().trim()
            if (targetText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a target count", Toast.LENGTH_SHORT).show()
                return
            }
            try {
                val count = targetText.toInt()
                if (count <= 0) {
                    Toast.makeText(requireContext(), "Target count must be greater than 0", Toast.LENGTH_SHORT).show()
                    return
                }
                count
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            1
        }
        
        onHabitAdded(name, type, targetCount)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
