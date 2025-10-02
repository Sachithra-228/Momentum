package com.example.habbittracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.ActivityMainBinding
import com.example.habbittracker.ui.fragments.HabitsFragment
import com.example.habbittracker.ui.fragments.MoodFragment
import com.example.habbittracker.ui.fragments.SettingsFragment
import com.example.habbittracker.ui.fragments.OnboardingFragment

/**
 * Main activity hosting the bottom navigation with three tabs:
 * Habits, Mood, and Settings
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesHelper: PreferencesHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        preferencesHelper = PreferencesHelper(this)
        
        // Check for new day and reset habits if needed
        preferencesHelper.checkAndResetForNewDay()
        
        // Load demo data if first run
        if (!preferencesHelper.isDemoDataLoaded()) {
            loadDemoData()
            preferencesHelper.setDemoDataLoaded(true)
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()

        // Decide start destination: onboarding vs app
        if (savedInstanceState == null) {
            if (preferencesHelper.isOnboardingDone()) {
                loadFragment(HabitsFragment())
                binding.bottomNavigation.selectedItemId = R.id.nav_habits
            } else {
                // Show onboarding and hide bottom nav
                loadFragment(OnboardingFragment())
                binding.bottomNavigation.visibility = android.view.View.GONE
            }
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    loadFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    loadFragment(MoodFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToBottomTab(menuId: Int) {
        binding.bottomNavigation.visibility = android.view.View.VISIBLE
        binding.bottomNavigation.selectedItemId = menuId
    }
    
    /**
     * Load demo data for first-time users
     */
    private fun loadDemoData() {
        // Create sample habits
        val sampleHabits = listOf(
            com.example.habbittracker.data.Habit.create("Drink Water", "countable", 8),
            com.example.habbittracker.data.Habit.create("Exercise", "single"),
            com.example.habbittracker.data.Habit.create("Meditate", "single")
        )
        preferencesHelper.saveHabits(sampleHabits)
        
        // Create sample mood entries
        val sampleMoods = listOf(
            com.example.habbittracker.data.MoodEntry.create("😊", "Feeling good today!"),
            com.example.habbittracker.data.MoodEntry.create("😄", "Great workout!"),
            com.example.habbittracker.data.MoodEntry.create("😐", "Just okay")
        )
        preferencesHelper.saveMoods(sampleMoods)
    }
    
    /**
     * Get preferences helper instance
     */
    fun getPreferencesHelper(): PreferencesHelper = preferencesHelper
    
    /**
     * Complete onboarding and show main app
     */
    fun completeOnboarding() {
        preferencesHelper.setOnboardingDone(true)
        loadFragment(HabitsFragment())
        binding.bottomNavigation.visibility = android.view.View.VISIBLE
        binding.bottomNavigation.selectedItemId = R.id.nav_habits
    }
    
    /**
     * Move to next onboarding screen
     */
    fun nextOnboardingScreen() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is OnboardingFragment) {
            currentFragment.nextScreen()
        }
    }
}