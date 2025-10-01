package com.example.habbittracker.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.habbittracker.databinding.DialogAddMoodBinding

/**
 * Dialog for adding mood entries with emoji and optional note
 */
class AddMoodDialog(
    private val selectedEmoji: String,
    private val onMoodAdded: (emoji: String, note: String?) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogAddMoodBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddMoodBinding.inflate(layoutInflater)
        
        setupUI()
        
        return androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Add Mood")
            .setPositiveButton("Save") { _, _ -> saveMood() }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupUI() {
        binding.textSelectedEmoji.text = selectedEmoji
        binding.textSelectedEmoji.textSize = 32f
    }
    
    private fun saveMood() {
        val note = binding.editTextNote.text.toString().trim()
        val finalNote = if (note.isEmpty()) null else note
        
        onMoodAdded(selectedEmoji, finalNote)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
