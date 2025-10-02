package com.example.habbittracker.ui.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.example.habbittracker.data.BreathingExercise
import com.example.habbittracker.data.BreathingSession
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentBreathingSessionBinding
import java.text.SimpleDateFormat
import java.util.*

class BreathingSessionFragment : Fragment() {

    private var _binding: FragmentBreathingSessionBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var exercise: BreathingExercise
    
    private var isRunning = false
    private var isPaused = false
    private var currentPhase = BreathingPhase.INHALE
    private var phaseTimeRemaining = 0
    private var sessionStartTime = 0L
    private var sessionDuration = 0L
    private var cyclesCompleted = 0
    private var stressLevelBefore = 5 // Default stress level
    
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    
    private var inhaleAnimator: ObjectAnimator? = null
    private var exhaleAnimator: ObjectAnimator? = null

    enum class BreathingPhase {
        INHALE, HOLD_IN, EXHALE, HOLD_OUT
    }

    companion object {
        private const val ARG_EXERCISE = "exercise"
        
        fun newInstance(exercise: BreathingExercise): BreathingSessionFragment {
            val fragment = BreathingSessionFragment()
            val args = Bundle()
            args.putSerializable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreathingSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        exercise = arguments?.getSerializable(ARG_EXERCISE) as BreathingExercise
        
        setupUI()
        setupClickListeners()
        startSession()
    }

    private fun setupUI() {
        binding.textExerciseName.text = exercise.name
        binding.textExerciseDescription.text = exercise.description
        binding.textTargetTime.text = formatTime(exercise.totalCycleSeconds * 10) // 10 cycles default
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        binding.btnPauseResume.setOnClickListener {
            if (isPaused) {
                resumeSession()
            } else {
                pauseSession()
            }
        }
        
        binding.btnStop.setOnClickListener {
            stopSession()
        }
        
        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun startSession() {
        isRunning = true
        isPaused = false
        sessionStartTime = System.currentTimeMillis()
        currentPhase = BreathingPhase.INHALE
        phaseTimeRemaining = exercise.inhaleSeconds
        
        updatePhaseUI()
        startPhaseTimer()
        startBreathingAnimation()
        
        binding.btnPauseResume.text = "Pause"
    }

    private fun pauseSession() {
        isPaused = true
        binding.btnPauseResume.text = "Resume"
        stopBreathingAnimation()
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun resumeSession() {
        isPaused = false
        binding.btnPauseResume.text = "Pause"
        startPhaseTimer()
        startBreathingAnimation()
    }

    private fun stopSession() {
        isRunning = false
        isPaused = false
        sessionDuration = System.currentTimeMillis() - sessionStartTime
        
        stopBreathingAnimation()
        timerRunnable?.let { handler.removeCallbacks(it) }
        
        saveSession()
        showSessionCompleteDialog()
    }

    private fun startPhaseTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (!isRunning || isPaused) return
                
                phaseTimeRemaining--
                updateTimerDisplay()
                
                if (phaseTimeRemaining <= 0) {
                    nextPhase()
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(timerRunnable!!)
    }

    private fun nextPhase() {
        currentPhase = when (currentPhase) {
            BreathingPhase.INHALE -> BreathingPhase.HOLD_IN
            BreathingPhase.HOLD_IN -> BreathingPhase.EXHALE
            BreathingPhase.EXHALE -> BreathingPhase.HOLD_OUT
            BreathingPhase.HOLD_OUT -> {
                cyclesCompleted++
                updateCyclesDisplay()
                BreathingPhase.INHALE
            }
        }
        
        phaseTimeRemaining = when (currentPhase) {
            BreathingPhase.INHALE -> exercise.inhaleSeconds
            BreathingPhase.HOLD_IN -> exercise.holdSeconds
            BreathingPhase.EXHALE -> exercise.exhaleSeconds
            BreathingPhase.HOLD_OUT -> exercise.pauseSeconds
        }
        
        updatePhaseUI()
        updateBreathingAnimation()
    }

    private fun updatePhaseUI() {
        binding.textBreathingPhase.text = when (currentPhase) {
            BreathingPhase.INHALE -> "Breathe In"
            BreathingPhase.HOLD_IN -> "Hold"
            BreathingPhase.EXHALE -> "Breathe Out"
            BreathingPhase.HOLD_OUT -> "Pause"
        }
    }

    private fun updateTimerDisplay() {
        binding.textTimer.text = phaseTimeRemaining.toString()
    }

    private fun updateCyclesDisplay() {
        binding.textCyclesCompleted.text = cyclesCompleted.toString()
    }

    private fun startBreathingAnimation() {
        updateBreathingAnimation()
    }

    private fun updateBreathingAnimation() {
        stopBreathingAnimation()
        
        val scaleFrom = if (currentPhase == BreathingPhase.INHALE) 0.8f else 1.2f
        val scaleTo = if (currentPhase == BreathingPhase.INHALE) 1.2f else 0.8f
        
        val animator = ObjectAnimator.ofFloat(binding.innerCircle, "scaleX", scaleFrom, scaleTo)
        animator.duration = phaseTimeRemaining * 1000L
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.repeatCount = 0
        
        val animatorY = ObjectAnimator.ofFloat(binding.innerCircle, "scaleY", scaleFrom, scaleTo)
        animatorY.duration = phaseTimeRemaining * 1000L
        animatorY.interpolator = AccelerateDecelerateInterpolator()
        animatorY.repeatCount = 0
        
        animator.start()
        animatorY.start()
        
        inhaleAnimator = animator
        exhaleAnimator = animatorY
    }

    private fun stopBreathingAnimation() {
        inhaleAnimator?.cancel()
        exhaleAnimator?.cancel()
    }

    private fun saveSession() {
        val session = BreathingSession(
            timestamp = sessionStartTime,
            exerciseId = exercise.id,
            durationMinutes = (sessionDuration / 60000).toInt(),
            cyclesCompleted = cyclesCompleted,
            stressLevelBefore = stressLevelBefore,
            stressLevelAfter = maxOf(1, stressLevelBefore - 2) // Assume improvement
        )
        
        val sessions = preferencesHelper.getBreathingSessions().toMutableList()
        sessions.add(session)
        preferencesHelper.saveBreathingSessions(sessions)
    }

    private fun showSessionCompleteDialog() {
        val duration = sessionDuration / 60000
        val message = "Great job! You completed $cyclesCompleted cycles in ${duration}min"
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Session Complete")
            .setMessage(message)
            .setPositiveButton("Done") { _, _ ->
                requireActivity().onBackPressed()
            }
            .setNegativeButton("Another Session") { _, _ ->
                startSession()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog() {
        // TODO: Implement settings dialog for customizing session
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopBreathingAnimation()
        timerRunnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }
}
