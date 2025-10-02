package com.example.habbittracker.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbittracker.R
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentSettingsBinding
import com.example.habbittracker.work.HydrationWorker
import java.util.concurrent.TimeUnit
import com.example.habbittracker.ui.fragments.ProfileFragment

/**
 * Fragment for app settings including hydration reminders and appearance customization
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var workManager: WorkManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        workManager = WorkManager.getInstance(requireContext())
        
        setupHydrationSettings()
        setupAppearanceSettings()
        setupAboutSection()
        setupDemoDataReset()
        binding.btnViewProfile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun setupHydrationSettings() {
        // Enable/Disable switch
        binding.switchHydrationEnabled.isChecked = preferencesHelper.isHydrationEnabled()
        binding.switchHydrationEnabled.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setHydrationEnabled(isChecked)
            updateHydrationWork(isChecked)
            updateHydrationUI()
        }
        
        // Interval spinner
        val intervals = listOf(30, 60, 120, 180) // minutes
        val intervalLabels = listOf("30 minutes", "1 hour", "2 hours", "3 hours")
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, intervalLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHydrationInterval.adapter = adapter
        
        val currentInterval = preferencesHelper.getHydrationIntervalMinutes()
        val intervalIndex = intervals.indexOf(currentInterval)
        if (intervalIndex != -1) {
            binding.spinnerHydrationInterval.setSelection(intervalIndex)
        }
        
        binding.spinnerHydrationInterval.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newInterval = intervals[position]
                if (newInterval != preferencesHelper.getHydrationIntervalMinutes()) {
                    preferencesHelper.setHydrationIntervalMinutes(newInterval)
                    if (preferencesHelper.isHydrationEnabled()) {
                        updateHydrationWork(true)
                    }
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        updateHydrationUI()
    }
    
    private fun setupAppearanceSettings() {
        // App name
        binding.editTextAppName.setText(preferencesHelper.getAppName())
        binding.btnSaveAppName.setOnClickListener {
            val newName = binding.editTextAppName.text.toString().trim()
            if (newName.isNotEmpty()) {
                preferencesHelper.setAppName(newName)
                Toast.makeText(requireContext(), "App name updated", Toast.LENGTH_SHORT).show()
                // Note: In a real app, you might want to restart the activity to update the title
            } else {
                Toast.makeText(requireContext(), "App name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Primary color
        val colors = listOf(
            "#B3E5FC", // Light Blue (default)
            "#81D4FA", // Light Blue variant
            "#9C27B0", // Purple
            "#673AB7", // Deep Purple
            "#3F51B5", // Indigo
            "#2196F3", // Blue
            "#00BCD4", // Cyan
            "#4CAF50", // Green
            "#8BC34A", // Light Green
            "#FFC107", // Amber
            "#FF9800", // Orange
            "#F44336"  // Red
        )
        
        val colorLabels = listOf(
            "Light Blue", "Sky Blue", "Purple", "Deep Purple", "Indigo", "Blue",
            "Cyan", "Green", "Light Green", "Amber", "Orange", "Red"
        )
        
        val colorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, colorLabels)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrimaryColor.adapter = colorAdapter
        
        val currentColor = preferencesHelper.getPrimaryColorHex()
        val colorIndex = colors.indexOf(currentColor)
        if (colorIndex != -1) {
            binding.spinnerPrimaryColor.setSelection(colorIndex)
        }
        
        binding.spinnerPrimaryColor.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newColor = colors[position]
                if (newColor != preferencesHelper.getPrimaryColorHex()) {
                    preferencesHelper.setPrimaryColorHex(newColor)
                    Toast.makeText(requireContext(), "Color updated. Restart app to see changes.", Toast.LENGTH_LONG).show()
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
    
    private fun setupAboutSection() {
        binding.textVersionName.text = "Version ${requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName}"
    }
    
    private fun setupDemoDataReset() {
        binding.btnResetDemoData.setOnClickListener {
            showResetConfirmationDialog()
        }
    }
    
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Demo Data")
            .setMessage("This will delete all habits and mood entries. Are you sure?")
            .setPositiveButton("Reset") { _, _ ->
                resetDemoData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun resetDemoData() {
        preferencesHelper.clearAllData()
        preferencesHelper.setDemoDataLoaded(false)
        
        // Load fresh demo data
        val sampleHabits = listOf(
            com.example.habbittracker.data.Habit.create("Drink Water", "countable", 8),
            com.example.habbittracker.data.Habit.create("Exercise", "single"),
            com.example.habbittracker.data.Habit.create("Meditate", "single")
        )
        preferencesHelper.saveHabits(sampleHabits)
        
        val sampleMoods = listOf(
            com.example.habbittracker.data.MoodEntry.create("😊", "Feeling good today!"),
            com.example.habbittracker.data.MoodEntry.create("😄", "Great workout!"),
            com.example.habbittracker.data.MoodEntry.create("😐", "Just okay")
        )
        preferencesHelper.saveMoods(sampleMoods)
        
        preferencesHelper.setDemoDataLoaded(true)
        
        Toast.makeText(requireContext(), "Demo data reset successfully", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateHydrationUI() {
        val isEnabled = preferencesHelper.isHydrationEnabled()
        binding.spinnerHydrationInterval.isEnabled = isEnabled
        binding.textHydrationStatus.text = if (isEnabled) {
            "Hydration reminders are enabled"
        } else {
            "Hydration reminders are disabled"
        }
    }
    
    private fun updateHydrationWork(enabled: Boolean) {
        // Cancel existing work
        workManager.cancelUniqueWork(HydrationWorker.WORK_NAME)
        
        if (enabled) {
            val intervalMinutes = preferencesHelper.getHydrationIntervalMinutes()
            
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            
            val hydrationWork = PeriodicWorkRequestBuilder<HydrationWorker>(
                intervalMinutes.toLong(),
                TimeUnit.MINUTES,
                15, // Flex interval
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                HydrationWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                hydrationWork
            )
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
