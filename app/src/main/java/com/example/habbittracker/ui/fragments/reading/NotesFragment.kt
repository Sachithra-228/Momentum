package com.example.habbittracker.ui.fragments.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.data.ReadingNote
import com.example.habbittracker.databinding.FragmentNotesBinding
import com.example.habbittracker.ui.adapters.NotesAdapter

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        setupList()
        binding.btnSaveNote.setOnClickListener {
            val text = binding.inputNote.text?.toString()?.trim().orEmpty()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a note", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val notes = preferencesHelper.getReadingNotes().toMutableList()
            val newNote = ReadingNote(java.util.UUID.randomUUID().toString(), System.currentTimeMillis(), text)
            notes.add(0, newNote)
            preferencesHelper.saveReadingNotes(notes)
            binding.inputNote.setText("")
            Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
            refreshNotes()
        }
    }

    private fun setupList() {
        notesAdapter = NotesAdapter(preferencesHelper.getReadingNotes())
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }
    }

    private fun refreshNotes() {
        notesAdapter.submitList(preferencesHelper.getReadingNotes())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


