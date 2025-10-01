package com.example.habbittracker.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a mood entry with emoji and optional note
 * @param id Unique identifier for the mood entry
 * @param emoji Emoji representing the mood
 * @param timestamp Unix timestamp when the mood was recorded
 * @param note Optional note accompanying the mood
 */
data class MoodEntry(
    val id: String,
    val emoji: String,
    val timestamp: Long,
    val note: String? = null
) {
    
    /**
     * Get formatted date string (yyyy-MM-dd)
     */
    fun getDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Get formatted time string (HH:mm)
     */
    fun getTimeString(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }
    
    /**
     * Get formatted date and time string
     */
    fun getDateTimeString(): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateTimeFormat.format(Date(timestamp))
    }
    
    /**
     * Check if this mood entry is from today
     */
    fun isToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return getDateString() == today
    }
    
    /**
     * Check if this mood entry is from yesterday
     */
    fun isYesterday(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        return getDateString() == yesterday
    }
    
    companion object {
        /**
         * Create a new mood entry with current timestamp
         */
        fun create(emoji: String, note: String? = null): MoodEntry {
            return MoodEntry(
                id = UUID.randomUUID().toString(),
                emoji = emoji,
                timestamp = System.currentTimeMillis(),
                note = note
            )
        }
        
        /**
         * Get available emoji options for mood selection
         */
        fun getAvailableEmojis(): List<String> {
            return listOf(
                "😊", "😄", "😍", "🥰", "😎", "🤔",
                "😐", "😑", "😔", "😢", "😡", "🤬"
            )
        }
    }
}
