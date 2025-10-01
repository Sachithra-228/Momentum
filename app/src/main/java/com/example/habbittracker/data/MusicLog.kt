package com.example.habbittracker.data

/**
 * Lightweight log entry for music engagement.
 */
data class MusicLog(
    val timestamp: Long,
    val minutes: Int,
    val action: String, // listen|sing|playlist|timer
    val song: String? = null,
    val emotion: String? = null,
    val intensity: Int? = null,
    val notes: String? = null
)


