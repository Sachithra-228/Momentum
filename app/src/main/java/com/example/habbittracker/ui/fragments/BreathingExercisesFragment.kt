package com.example.habbittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.data.BreathingExercise
import com.example.habbittracker.data.BreathingExercises
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentBreathingExercisesBinding
import com.example.habbittracker.ui.adapters.BreathingExercisesAdapter

class BreathingExercisesFragment : Fragment() {

    private var _binding: FragmentBreathingExercisesBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var adapter: BreathingExercisesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreathingExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        
        setupRecyclerView()
        setupQuickStartButtons()
        updateStatistics()
        loadExercises()
    }

    private fun setupRecyclerView() {
        adapter = BreathingExercisesAdapter { exercise ->
            startBreathingSession(exercise)
        }
        
        binding.rvBreathingExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBreathingExercises.adapter = adapter
    }

    private fun setupQuickStartButtons() {
        binding.btnQuick478.setOnClickListener {
            val exercise = BreathingExercises.exercises.find { it.id == "478_breathing" }
            exercise?.let { startBreathingSession(it) }
        }
        
        binding.btnQuickBox.setOnClickListener {
            val exercise = BreathingExercises.exercises.find { it.id == "box_breathing" }
            exercise?.let { startBreathingSession(it) }
        }
        
        binding.btnEmergencyBreathing.setOnClickListener {
            val exercise = BreathingExercises.exercises.find { it.id == "calming_breath" }
            exercise?.let { startBreathingSession(it) }
        }
    }

    private fun loadExercises() {
        adapter.updateExercises(BreathingExercises.exercises)
    }

    private fun startBreathingSession(exercise: BreathingExercise) {
        val sessionFragment = BreathingSessionFragment.newInstance(exercise)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.habbittracker.R.id.fragment_container, sessionFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateStatistics() {
        val sessions = preferencesHelper.getBreathingSessions()
        val now = System.currentTimeMillis()
        val weekMs = 7 * 24 * 60 * 60 * 1000L
        
        // Weekly minutes
        val weekMinutes = sessions.filter { now - it.timestamp <= weekMs }.sumOf { it.durationMinutes }
        binding.textWeeklyMinutes.text = weekMinutes.toString()
        
        // Total sessions
        binding.textTotalSessions.text = sessions.size.toString()
        
        // Streak calculation
        val streak = calculateStreak(sessions)
        binding.textStreakDays.text = streak.toString()
    }

    private fun calculateStreak(sessions: List<com.example.habbittracker.data.BreathingSession>): Int {
        if (sessions.isEmpty()) return 0
        
        val days = sessions.map { 
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(it.timestamp)) 
        }.toSet().sortedDescending()
        
        var streak = 0
        val cal = java.util.Calendar.getInstance()
        
        for (day in days) {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(cal.time)
            
            if (day == today || day == java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date(cal.timeInMillis - 24 * 60 * 60 * 1000L))) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return streak
    }

    override fun onResume() {
        super.onResume()
        updateStatistics()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
