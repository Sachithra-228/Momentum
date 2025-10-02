package com.example.habbittracker.data

import java.io.Serializable

/**
 * Data class representing a breathing exercise technique
 */
data class BreathingExercise(
    val id: String,
    val name: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdSeconds: Int,
    val exhaleSeconds: Int,
    val pauseSeconds: Int = 0,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val benefits: List<String> = emptyList(),
    val icon: String = "🌬️"
) : Serializable {
    enum class Difficulty : Serializable {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    val totalCycleSeconds: Int
        get() = inhaleSeconds + holdSeconds + exhaleSeconds + pauseSeconds
}

/**
 * Data class for tracking breathing session logs
 */
data class BreathingSession(
    val timestamp: Long,
    val exerciseId: String,
    val durationMinutes: Int,
    val cyclesCompleted: Int,
    val stressLevelBefore: Int, // 1-10 scale
    val stressLevelAfter: Int, // 1-10 scale
    val notes: String? = null,
    val mood: String? = null
)

/**
 * Predefined breathing exercises
 */
object BreathingExercises {
    val exercises = listOf(
        BreathingExercise(
            id = "478_breathing",
            name = "4-7-8 Breathing",
            description = "Calming technique for anxiety relief and better sleep",
            inhaleSeconds = 4,
            holdSeconds = 7,
            exhaleSeconds = 8,
            difficulty = BreathingExercise.Difficulty.BEGINNER,
            benefits = listOf("Reduces anxiety", "Improves sleep", "Calms nervous system"),
            icon = "🌙"
        ),
        BreathingExercise(
            id = "box_breathing",
            name = "Box Breathing",
            description = "Military technique for focus and stress management",
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 4,
            pauseSeconds = 4,
            difficulty = BreathingExercise.Difficulty.BEGINNER,
            benefits = listOf("Improves focus", "Reduces stress", "Enhances performance"),
            icon = "📦"
        ),
        BreathingExercise(
            id = "belly_breathing",
            name = "Belly Breathing",
            description = "Deep diaphragmatic breathing for relaxation",
            inhaleSeconds = 5,
            holdSeconds = 2,
            exhaleSeconds = 6,
            difficulty = BreathingExercise.Difficulty.BEGINNER,
            benefits = listOf("Deep relaxation", "Reduces tension", "Improves oxygen flow"),
            icon = "🫁"
        ),
        BreathingExercise(
            id = "calming_breath",
            name = "Calming Breath",
            description = "Gentle breathing with longer exhales",
            inhaleSeconds = 4,
            holdSeconds = 2,
            exhaleSeconds = 6,
            difficulty = BreathingExercise.Difficulty.BEGINNER,
            benefits = listOf("Quick relaxation", "Stress relief", "Mindfulness"),
            icon = "🌸"
        ),
        BreathingExercise(
            id = "triangle_breathing",
            name = "Triangle Breathing",
            description = "Equal timing for balance and centering",
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 4,
            difficulty = BreathingExercise.Difficulty.INTERMEDIATE,
            benefits = listOf("Mental balance", "Emotional stability", "Concentration"),
            icon = "🔺"
        ),
        BreathingExercise(
            id = "extended_exhale",
            name = "Extended Exhale",
            description = "Advanced technique with longer exhales",
            inhaleSeconds = 4,
            holdSeconds = 4,
            exhaleSeconds = 8,
            difficulty = BreathingExercise.Difficulty.ADVANCED,
            benefits = listOf("Deep relaxation", "Advanced stress relief", "Better sleep"),
            icon = "🌊"
        )
    )
}
