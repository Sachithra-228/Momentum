package com.example.habbittracker.ui.fragments.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.habbittracker.data.Habit
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentBookChapterBinding

class BookChapterFragment : Fragment() {
    private var _binding: FragmentBookChapterBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnLogChapter.setOnClickListener { logReadingHabit() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logReadingHabit() {
        val habits = preferencesHelper.getHabits().toMutableList()
        val readingIndex = habits.indexOfFirst { it.name.equals("Reading", true) }
        if (readingIndex == -1) {
            val newHabit = Habit.create("Reading", "countable", 1)
            newHabit.incrementCount()
            habits.add(newHabit)
        } else {
            val habit = habits[readingIndex]
            if (habit.type != "countable") {
                habits[readingIndex] = habit.copy(type = "countable", targetCount = 1, currentCountToday = 0)
            }
            habits[readingIndex].incrementCount()
        }
        preferencesHelper.saveHabits(habits)
        Toast.makeText(requireContext(), "Reading logged", Toast.LENGTH_SHORT).show()
    }
}


