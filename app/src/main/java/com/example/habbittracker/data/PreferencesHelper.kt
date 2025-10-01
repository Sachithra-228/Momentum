package com.example.habbittracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for managing SharedPreferences data persistence
 * Handles serialization of habits and mood entries using Gson
 */
class PreferencesHelper(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "habbit_tracker_prefs"
        
        // Keys for different data types
        private const val KEY_HABITS = "habits_json"
        private const val KEY_MOODS = "moods_json"
        private const val KEY_HYDRATION_ENABLED = "hydration_enabled"
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval_minutes"
        private const val KEY_THEME_PRIMARY_COLOR = "theme_primary_color_hex"
        private const val KEY_APP_NAME = "app_name"
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_PROFILE_EMAIL = "profile_email"
        private const val KEY_LAST_OPEN_DATE = "app_last_open_date"
        private const val KEY_DEMO_DATA_LOADED = "demo_data_loaded"
        private const val KEY_READING_NOTES = "reading_notes_json"
        
        // Default values
        private const val DEFAULT_HYDRATION_ENABLED = false
        private const val DEFAULT_HYDRATION_INTERVAL = 60 // 1 hour
        private const val DEFAULT_PRIMARY_COLOR = "#8D6E63" // Brown
        private const val DEFAULT_APP_NAME = "Habbit Tracker"
    }
    
    // Habits management
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }
    
    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    // Mood entries management
    fun saveMoods(moods: List<MoodEntry>) {
        val json = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, json).apply()
    }
    
    fun getMoods(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOODS, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<MoodEntry>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    // Hydration settings
    fun isHydrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_ENABLED, DEFAULT_HYDRATION_ENABLED)
    }

    // Reading notes
    fun saveReadingNotes(notes: List<ReadingNote>) {
        val json = gson.toJson(notes)
        prefs.edit().putString(KEY_READING_NOTES, json).apply()
    }

    fun getReadingNotes(): List<ReadingNote> {
        val json = prefs.getString(KEY_READING_NOTES, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<ReadingNote>>() {}.type
                gson.fromJson<List<ReadingNote>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }
    
    fun getHydrationIntervalMinutes(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL, DEFAULT_HYDRATION_INTERVAL)
    }
    
    fun setHydrationIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, minutes).apply()
    }
    
    // Theme settings
    fun getPrimaryColorHex(): String {
        return prefs.getString(KEY_THEME_PRIMARY_COLOR, DEFAULT_PRIMARY_COLOR) ?: DEFAULT_PRIMARY_COLOR
    }
    
    fun setPrimaryColorHex(colorHex: String) {
        prefs.edit().putString(KEY_THEME_PRIMARY_COLOR, colorHex).apply()
    }
    
    fun getAppName(): String {
        return prefs.getString(KEY_APP_NAME, DEFAULT_APP_NAME) ?: DEFAULT_APP_NAME
    }
    
    fun setAppName(name: String) {
        prefs.edit().putString(KEY_APP_NAME, name).apply()
    }

    // Profile
    fun getProfileName(): String {
        return prefs.getString(KEY_PROFILE_NAME, "Your Name") ?: "Your Name"
    }

    fun setProfileName(name: String) {
        prefs.edit().putString(KEY_PROFILE_NAME, name).apply()
    }

    fun getProfileEmail(): String {
        return prefs.getString(KEY_PROFILE_EMAIL, "you@example.com") ?: "you@example.com"
    }

    fun setProfileEmail(email: String) {
        prefs.edit().putString(KEY_PROFILE_EMAIL, email).apply()
    }
    
    // Last open date for day rollover logic
    fun getLastOpenDate(): String? {
        return prefs.getString(KEY_LAST_OPEN_DATE, null)
    }
    
    fun setLastOpenDate(date: String) {
        prefs.edit().putString(KEY_LAST_OPEN_DATE, date).apply()
    }
    
    // Demo data flag
    fun isDemoDataLoaded(): Boolean {
        return prefs.getBoolean(KEY_DEMO_DATA_LOADED, false)
    }
    
    fun setDemoDataLoaded(loaded: Boolean) {
        prefs.edit().putBoolean(KEY_DEMO_DATA_LOADED, loaded).apply()
    }
    
    /**
     * Check if it's a new day and reset habit progress if needed
     * Returns true if habits were reset
     */
    fun checkAndResetForNewDay(): Boolean {
        val today = getCurrentDateString()
        val lastOpenDate = getLastOpenDate()
        
        if (lastOpenDate != today) {
            // New day detected, reset all habits
            val habits = getHabits().map { habit ->
                habit.copy(currentCountToday = 0, lastUpdatedDate = today)
            }
            saveHabits(habits)
            setLastOpenDate(today)
            return true
        }
        return false
    }
    
    /**
     * Get current date string in yyyy-MM-dd format
     */
    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * Clear all data (for demo reset)
     */
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
    
    /**
     * Get mood entries for the last N days
     */
    fun getMoodsForLastDays(days: Int): List<MoodEntry> {
        val allMoods = getMoods().sortedByDescending { it.timestamp }
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return allMoods.filter { it.timestamp >= cutoffTime }
    }
    
    /**
     * Get mood summary for sharing (last 7 days)
     */
    fun getMoodSummaryForSharing(): String {
        val last7DaysMoods = getMoodsForLastDays(7)
        val emojiCounts = mutableMapOf<String, Int>()
        
        last7DaysMoods.forEach { mood ->
            emojiCounts[mood.emoji] = emojiCounts.getOrDefault(mood.emoji, 0) + 1
        }
        
        if (emojiCounts.isEmpty()) {
            return "No mood entries in the last 7 days."
        }
        
        val summary = StringBuilder()
        summary.append("Mood Summary (Last 7 Days):\n\n")
        
        emojiCounts.toList().sortedByDescending { it.second }.forEach { (emoji, count) ->
            summary.append("$emoji: $count times\n")
        }
        
        summary.append("\nTotal entries: ${last7DaysMoods.size}")
        return summary.toString()
    }
}
