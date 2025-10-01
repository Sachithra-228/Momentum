package com.example.habbittracker.ui.fragments.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.os.CountDownTimer
import android.widget.Toast
import com.example.habbittracker.data.Habit
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentArticleBinding
import java.util.Locale

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper
    private var countDownTimer: CountDownTimer? = null
    private var remainingMillis: Long = 0L
    private var isRunning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnStartTimer.setOnClickListener { startTimerAndAutoLog(15) }
        binding.btnLogSession.setOnClickListener {
            logReadingHabit()
        }
        binding.btnPauseResume.setOnClickListener { togglePauseResume() }
        binding.btnCancelTimer.setOnClickListener { cancelTimer(resetText = true) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }

    private fun startTimerAndAutoLog(minutes: Int) {
        remainingMillis = minutes * 60 * 1000L
        startOrResumeTimer()
        Toast.makeText(requireContext(), "Timer started for $minutes min", Toast.LENGTH_SHORT).show()
    }

    private fun startOrResumeTimer() {
        countDownTimer?.cancel()
        isRunning = true
        binding.btnPauseResume.text = "Pause"
        countDownTimer = object : CountDownTimer(remainingMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateCountdownText(millisUntilFinished)
            }
            override fun onFinish() {
                isRunning = false
                remainingMillis = 0L
                updateCountdownText(0)
                logReadingHabit()
            }
        }.start()
    }

    private fun togglePauseResume() {
        if (remainingMillis <= 0L) return
        if (isRunning) {
            // Pause
            countDownTimer?.cancel()
            isRunning = false
            binding.btnPauseResume.text = "Resume"
        } else {
            // Resume
            startOrResumeTimer()
        }
    }

    private fun cancelTimer(resetText: Boolean) {
        countDownTimer?.cancel()
        isRunning = false
        remainingMillis = 0L
        if (resetText) updateCountdownText(15 * 60 * 1000L)
        binding.btnPauseResume.text = "Pause"
    }

    private fun updateCountdownText(millis: Long) {
        val totalSeconds = (millis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        binding.textCountdown.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun logReadingHabit() {
        val habits = preferencesHelper.getHabits().toMutableList()
        // Find or create a generic Reading habit (countable, target 1)
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
        Toast.makeText(requireContext(), "Reading session logged", Toast.LENGTH_SHORT).show()
    }
}


