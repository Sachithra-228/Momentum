package com.example.habbittracker.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a habit that can be tracked daily
 * @param id Unique identifier for the habit
 * @param name Display name of the habit
 * @param type Type of habit - "single" for one-time completion or "countable" for numeric targets
 * @param targetCount Target number for countable habits (default 1)
 * @param currentCountToday Current progress for today (resets daily)
 * @param lastUpdatedDate Last date when the habit was updated (yyyy-MM-dd format)
 */
data class Habit(
    val id: String,
    var name: String,
    var type: String, // "single" or "countable"
    var targetCount: Int = 1,
    var currentCountToday: Int = 0,
    var lastUpdatedDate: String // yyyy-MM-dd format
) {
    
    /**
     * Check if the habit is completed for today
     */
    fun isCompletedToday(): Boolean {
        return when (type) {
            "single" -> currentCountToday >= 1
            "countable" -> currentCountToday >= targetCount
            else -> false
        }
    }
    
    /**
     * Get progress percentage for today (0-100)
     */
    fun getProgressPercentage(): Float {
        return when (type) {
            "single" -> if (currentCountToday >= 1) 100f else 0f
            "countable" -> {
                if (targetCount <= 0) return 0f
                (currentCountToday.toFloat() / targetCount.toFloat() * 100f).coerceAtMost(100f)
            }
            else -> 0f
        }
    }
    
    /**
     * Increment the current count for countable habits
     */
    fun incrementCount(): Boolean {
        if (type == "countable" && currentCountToday < targetCount) {
            currentCountToday++
            updateLastUpdatedDate()
            return true
        }
        return false
    }
    
    /**
     * Decrement the current count for countable habits
     */
    fun decrementCount(): Boolean {
        if (type == "countable" && currentCountToday > 0) {
            currentCountToday--
            updateLastUpdatedDate()
            return true
        }
        return false
    }
    
    /**
     * Mark as completed for single habits
     */
    fun markCompleted(): Boolean {
        if (type == "single" && currentCountToday < 1) {
            currentCountToday = 1
            updateLastUpdatedDate()
            return true
        }
        return false
    }
    
    /**
     * Reset progress for a new day
     */
    fun resetForNewDay() {
        currentCountToday = 0
        updateLastUpdatedDate()
    }
    
    /**
     * Update the last updated date to today
     */
    private fun updateLastUpdatedDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        lastUpdatedDate = dateFormat.format(Date())
    }
    
    companion object {
        /**
         * Create a new habit with today's date
         */
        fun create(name: String, type: String, targetCount: Int = 1): Habit {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return Habit(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                targetCount = targetCount,
                currentCountToday = 0,
                lastUpdatedDate = dateFormat.format(Date())
            )
        }
    }
}
