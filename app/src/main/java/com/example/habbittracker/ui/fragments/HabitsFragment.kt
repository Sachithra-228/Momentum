package com.example.habbittracker.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.habbittracker.R
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.habbittracker.data.Habit
import com.example.habbittracker.data.MusicLog
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentHabitsBinding
import com.example.habbittracker.ui.adapters.HabitsAdapter
import com.example.habbittracker.ui.dialogs.AddHabitDialog
// Removed reading feature imports

/**
 * Fragment for managing daily habits
 * Shows a list of habits with progress tracking and allows adding/editing/deleting habits
 */
class HabitsFragment : Fragment() {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var habitsAdapter: HabitsAdapter
    private val habits = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        
        setupPieChart()
        setupRecyclerView()
        setupFab()
        setupMusicSection()
        loadHabits()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data and pie when returning from other screens that may have logged reading
        habits.clear()
        habits.addAll(preferencesHelper.getHabits())
        habitsAdapter.notifyDataSetChanged()
        updateOverallSummary()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            habits = habits,
            onHabitClick = { habit -> showEditHabitDialog(habit) },
            onHabitDelete = { habit -> showDeleteConfirmationDialog(habit) },
            onHabitIncrement = { habit -> incrementHabit(habit) },
            onHabitDecrement = { habit -> decrementHabit(habit) },
            onHabitToggle = { habit -> toggleHabit(habit) }
        )
        
        binding.rvHabits.apply {
            adapter = habitsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        // Setup swipe to delete
        setupSwipeToDelete()
    }
    
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ): Boolean = false
            
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(habits[position])
                    habitsAdapter.notifyItemChanged(position) // Restore the item
                }
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.rvHabits)
    }
    
    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        habits.clear()
        habits.addAll(preferencesHelper.getHabits())
        habitsAdapter.notifyDataSetChanged()
        updateEmptyState()
        updateOverallSummary()
    }
    
    private fun updateEmptyState() {
        if (habits.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvHabits.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvHabits.visibility = View.VISIBLE
        }
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddHabitDialog { name, type, targetCount ->
            addHabit(name, type, targetCount)
        }
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialog = AddHabitDialog(
            habit = habit,
            onHabitAdded = { name, type, targetCount ->
                editHabit(habit, name, type, targetCount)
            }
        )
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }
    
    private fun showDeleteConfirmationDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteHabit(habit)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addHabit(name: String, type: String, targetCount: Int) {
        val habit = Habit.create(name, type, targetCount)
        habits.add(habit)
        preferencesHelper.saveHabits(habits)
        habitsAdapter.notifyItemInserted(habits.size - 1)
        updateEmptyState()
        updateOverallSummary()
        Toast.makeText(requireContext(), "Habit added successfully", Toast.LENGTH_SHORT).show()
    }
    
    private fun editHabit(habit: Habit, name: String, type: String, targetCount: Int) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit.copy(
                name = name,
                type = type,
                targetCount = targetCount
            )
            preferencesHelper.saveHabits(habits)
            habitsAdapter.notifyItemChanged(index)
            updateOverallSummary()
            Toast.makeText(requireContext(), "Habit updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun deleteHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits.removeAt(index)
            preferencesHelper.saveHabits(habits)
            habitsAdapter.notifyItemRemoved(index)
            updateEmptyState()
            updateOverallSummary()
            Toast.makeText(requireContext(), "Habit deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun incrementHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1 && habit.incrementCount()) {
            habits[index] = habit
            preferencesHelper.saveHabits(habits)
            habitsAdapter.notifyItemChanged(index)
            updateOverallSummary()
        }
    }
    
    private fun decrementHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1 && habit.decrementCount()) {
            habits[index] = habit
            preferencesHelper.saveHabits(habits)
            habitsAdapter.notifyItemChanged(index)
            updateOverallSummary()
        }
    }
    
    private fun toggleHabit(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            if (habit.type == "single") {
                if (habit.markCompleted()) {
                    habits[index] = habit
                } else {
                    // Unmark completed
                    habits[index] = habit.copy(currentCountToday = 0)
                }
            } else {
                // For countable habits, just increment
                if (habit.incrementCount()) {
                    habits[index] = habit
                }
            }
            preferencesHelper.saveHabits(habits)
            habitsAdapter.notifyItemChanged(index)
            updateOverallSummary()
        }
    }

    // Music section
    private var musicTimerSecondsRemaining: Int = 600
    private var musicTimer: java.util.Timer? = null

    private fun setupMusicSection() {
        binding.chipListen.setOnClickListener {
            quickLog(action = "listen", minutes = 10)
        }
        binding.chipSing.setOnClickListener {
            quickLog(action = "sing", minutes = 10) // 3 songs ~ 10 min
        }
        binding.chipAddPlaylist.setOnClickListener {
            quickLog(action = "playlist", minutes = 1)
        }
        binding.chipViewPlaylist.setOnClickListener {
            navigateTo(PlaylistFragment())
        }

        binding.btnMusicTimer.setOnClickListener {
            if (musicTimer == null) startMusicTimer() else stopMusicTimer(saveOnStop = true)
        }
        binding.btnMusicLog.setOnClickListener {
            showMusicLogDialog()
        }

        updateMusicStats()
        updateMusicTimerLabel()
    }

    private fun startMusicTimer() {
        binding.btnMusicTimer.text = "Stop timer"
        musicTimerSecondsRemaining = 600
        musicTimer = java.util.Timer()
        musicTimer?.scheduleAtFixedRate(object : java.util.TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    musicTimerSecondsRemaining--
                    updateMusicTimerLabel()
                    if (musicTimerSecondsRemaining <= 0) {
                        stopMusicTimer(saveOnStop = true)
                    }
                }
            }
        }, 1000, 1000)
    }

    private fun stopMusicTimer(saveOnStop: Boolean) {
        musicTimer?.cancel()
        musicTimer = null
        binding.btnMusicTimer.text = "Start 10-min timer"
        if (saveOnStop) {
            saveMusicLog(MusicLog(System.currentTimeMillis(), 10, "timer"))
        }
        musicTimerSecondsRemaining = 600
        updateMusicTimerLabel()
        updateMusicStats()
    }

    private fun updateMusicTimerLabel() {
        val m = musicTimerSecondsRemaining / 60
        val s = musicTimerSecondsRemaining % 60
        binding.textMusicTimer.text = String.format("%02d:%02d", m, s)
    }

    private fun showMusicLogDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_music_log, null)
        val editSong = dialogView.findViewById<android.widget.EditText>(R.id.edit_song)
        val spinner = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_emotion)
        val seek = dialogView.findViewById<android.widget.SeekBar>(R.id.seek_intensity)
        val editNotes = dialogView.findViewById<android.widget.EditText>(R.id.edit_notes)

        val emotions = listOf("Happy", "Calm", "Melancholy", "Energized", "Reflective")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emotions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Quick Music Log")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                saveMusicLog(
                    MusicLog(
                        timestamp = System.currentTimeMillis(),
                        minutes = 10,
                        action = "log",
                        song = editSong.text.toString().takeIf { it.isNotBlank() },
                        emotion = emotions.getOrNull(spinner.selectedItemPosition),
                        intensity = seek.progress,
                        notes = editNotes.text.toString().takeIf { it.isNotBlank() }
                    )
                )
                updateMusicStats()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun quickLog(action: String, minutes: Int) {
        saveMusicLog(MusicLog(System.currentTimeMillis(), minutes, action))
        updateMusicStats()
        Toast.makeText(requireContext(), "Logged $minutes min", Toast.LENGTH_SHORT).show()
    }

    private fun saveMusicLog(entry: MusicLog) {
        val logs = preferencesHelper.getMusicLogs().toMutableList()
        logs.add(entry)
        preferencesHelper.saveMusicLogs(logs)
    }

    private fun updateMusicStats() {
        val logs = preferencesHelper.getMusicLogs()
        val now = System.currentTimeMillis()
        val weekMs = 7 * 24 * 60 * 60 * 1000L
        val weekMinutes = logs.filter { now - it.timestamp <= weekMs }.sumOf { it.minutes }

        // Simple daily streak based on any log on consecutive days
        val days = logs.map { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it.timestamp)) }
        val uniqueDays = days.toSet().toMutableSet()
        var streak = 0
        val cal = java.util.Calendar.getInstance()
        while (true) {
            val key = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
            if (uniqueDays.contains(key)) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        binding.textMusicStats.text = "This week: ${weekMinutes} min • Streak: ${streak} days"
    }

    private fun navigateTo(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupPieChart() {
        val pieChart = binding.pieOverall
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawCenterText(true)
        pieChart.isRotationEnabled = false
        pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChart.legend.isEnabled = false
        pieChart.holeRadius = 70f
        pieChart.transparentCircleRadius = 75f
    }

    private fun updateOverallSummary() {
        val pieChart = binding.pieOverall
        val total = habits.size
        val completed = habits.count { it.isCompletedToday() }

        val percent: Float = if (total == 0) 0f else (completed.toFloat() / total.toFloat()) * 100f

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(percent.coerceIn(0f, 100f), "Completed"))
        entries.add(PieEntry((100f - percent).coerceIn(0f, 100f), "Remaining"))

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawValues(false)
        dataSet.colors = listOf(
            resources.getColor(R.color.colorPrimary, null),
            resources.getColor(R.color.colorSecondary, null)
        )

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = "${percent.toInt()}%"
        pieChart.invalidate()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
