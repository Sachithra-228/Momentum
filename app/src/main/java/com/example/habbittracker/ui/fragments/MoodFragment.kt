package com.example.habbittracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.R
import com.example.habbittracker.data.MoodEntry
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentMoodBinding
import com.example.habbittracker.ui.adapters.MoodAdapter
import com.example.habbittracker.ui.dialogs.AddMoodDialog
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for mood journaling with emoji selector and chart visualization
 */
class MoodFragment : Fragment() {
    
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var moodAdapter: MoodAdapter
    private val moods = mutableListOf<MoodEntry>()
    private var selectedEmoji: String = "😊"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        
        setupEmojiSelector()
        setupChart()
        setupRecyclerView()
        setupButtons()
        loadMoods()
    }
    
    private fun setupEmojiSelector() {
        val emojis = MoodEntry.getAvailableEmojis()
        
        // Create emoji buttons dynamically
        val emojiContainer = binding.emojiSelector
        
        emojis.forEach { emoji ->
            val emojiButton = android.widget.Button(requireContext()).apply {
                text = emoji
                textSize = 24f
                setPadding(16, 16, 16, 16)
                background = null
                setOnClickListener {
                    selectedEmoji = emoji
                    updateEmojiSelection()
                }
            }
            emojiContainer.addView(emojiButton)
        }
        
        updateEmojiSelection()
    }
    
    private fun updateEmojiSelection() {
        for (i in 0 until binding.emojiSelector.childCount) {
            val child = binding.emojiSelector.getChildAt(i)
            if (child is android.widget.Button) {
                val emoji = child.text.toString()
                if (emoji == selectedEmoji) {
                    child.setBackgroundColor(requireContext().getColor(R.color.colorPrimary))
                } else {
                    child.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        }
    }
    
    private fun setupChart() {
        binding.chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            
            xAxis.apply {
                isEnabled = true
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }
            
            axisLeft.apply {
                isEnabled = true
                setDrawGridLines(true)
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moods,
            onEdit = { mood -> showEditMoodDialog(mood) },
            onDelete = { mood -> deleteMood(mood) }
        )
        
        binding.rvMoods.apply {
            adapter = moodAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupButtons() {
        binding.btnAddMood.setOnClickListener {
            showAddMoodDialog()
        }
        
        binding.btnShareMood.setOnClickListener {
            shareMoodSummary()
        }
    }
    
    private fun loadMoods() {
        moods.clear()
        moods.addAll(preferencesHelper.getMoods().sortedByDescending { it.timestamp })
        moodAdapter = MoodAdapter(moods,
            onEdit = { mood -> showEditMoodDialog(mood) },
            onDelete = { mood -> deleteMood(mood) }
        )
        binding.rvMoods.adapter = moodAdapter
        updateChart()
        updateEmptyState()
    }
    
    private fun updateChart() {
        val last7DaysMoods = preferencesHelper.getMoodsForLastDays(7)
        val moodCountsByDay = mutableMapOf<String, Int>()
        
        // Group moods by date and count them
        last7DaysMoods.forEach { mood ->
            val date = mood.getDateString()
            moodCountsByDay[date] = moodCountsByDay.getOrDefault(date, 0) + 1
        }
        
        // Create entries for the last 7 days
        val entries = mutableListOf<Entry>()
        val calendar = Calendar.getInstance()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val count = moodCountsByDay.getOrDefault(date, 0).toFloat()
            entries.add(Entry((6 - i).toFloat(), count))
        }
        
        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, "Mood Entries").apply {
                color = requireContext().getColor(R.color.colorPrimary)
                setCircleColor(requireContext().getColor(R.color.colorPrimary))
                lineWidth = 2f
                circleRadius = 4f
                setDrawCircleHole(true)
                setDrawValues(false)
            }
            
            val lineData = LineData(dataSet)
            binding.chart.data = lineData
            binding.chart.invalidate()
            binding.chart.visibility = View.VISIBLE
        } else {
            binding.chart.visibility = View.GONE
        }
    }
    
    private fun updateEmptyState() {
        if (moods.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvMoods.visibility = View.GONE
            binding.btnShareMood.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvMoods.visibility = View.VISIBLE
            binding.btnShareMood.visibility = View.VISIBLE
        }
    }
    
    private fun showAddMoodDialog() {
        val dialog = AddMoodDialog(selectedEmoji) { emoji, note ->
            addMood(emoji, note)
        }
        dialog.show(parentFragmentManager, "AddMoodDialog")
    }
    
    private fun addMood(emoji: String, note: String?) {
        val mood = MoodEntry.create(emoji, note)
        moods.add(0, mood)
        preferencesHelper.saveMoods(moods)
        moodAdapter = MoodAdapter(moods,
            onEdit = { m -> showEditMoodDialog(m) },
            onDelete = { m -> deleteMood(m) }
        )
        binding.rvMoods.adapter = moodAdapter
        updateChart()
        updateEmptyState()
        
        Toast.makeText(requireContext(), "Mood added successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showEditMoodDialog(mood: MoodEntry) {
        val dialog = AddMoodDialog(mood.emoji) { emoji, note ->
            val index = moods.indexOfFirst { it.id == mood.id }
            if (index != -1) {
                moods[index] = mood.copy(emoji = emoji, note = note)
                preferencesHelper.saveMoods(moods)
                loadMoods()
                Toast.makeText(requireContext(), "Mood updated", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "EditMoodDialog")
    }

    private fun deleteMood(mood: MoodEntry) {
        val index = moods.indexOfFirst { it.id == mood.id }
        if (index != -1) {
            moods.removeAt(index)
            preferencesHelper.saveMoods(moods)
            loadMoods()
            Toast.makeText(requireContext(), "Mood deleted", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareMoodSummary() {
        val summary = preferencesHelper.getMoodSummaryForSharing()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, summary)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
