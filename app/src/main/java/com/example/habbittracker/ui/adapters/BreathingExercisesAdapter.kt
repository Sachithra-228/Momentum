package com.example.habbittracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.data.BreathingExercise
import com.example.habbittracker.databinding.ItemBreathingExerciseBinding

class BreathingExercisesAdapter(
    private val onExerciseClick: (BreathingExercise) -> Unit
) : RecyclerView.Adapter<BreathingExercisesAdapter.BreathingExerciseViewHolder>() {

    private var exercises = listOf<BreathingExercise>()

    fun updateExercises(newExercises: List<BreathingExercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreathingExerciseViewHolder {
        val binding = ItemBreathingExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BreathingExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreathingExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size

    inner class BreathingExerciseViewHolder(
        private val binding: ItemBreathingExerciseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: BreathingExercise) {
            binding.textExerciseIcon.text = exercise.icon
            binding.textExerciseName.text = exercise.name
            binding.textExerciseDescription.text = exercise.description
            
            // Set difficulty badge
            binding.textDifficulty.text = exercise.difficulty.name
            binding.textDifficulty.background = when (exercise.difficulty) {
                BreathingExercise.Difficulty.BEGINNER -> 
                    binding.root.context.getDrawable(com.example.habbittracker.R.drawable.difficulty_beginner_background)
                BreathingExercise.Difficulty.INTERMEDIATE -> 
                    binding.root.context.getDrawable(com.example.habbittracker.R.drawable.difficulty_beginner_background)
                BreathingExercise.Difficulty.ADVANCED -> 
                    binding.root.context.getDrawable(com.example.habbittracker.R.drawable.difficulty_beginner_background)
            }
            
            // Set benefits
            if (exercise.benefits.isNotEmpty()) {
                binding.textBenefit1.text = exercise.benefits[0]
                binding.textBenefit1.visibility = android.view.View.VISIBLE
                
                if (exercise.benefits.size > 1) {
                    binding.textBenefit2.text = exercise.benefits[1]
                    binding.textBenefit2.visibility = android.view.View.VISIBLE
                } else {
                    binding.textBenefit2.visibility = android.view.View.GONE
                }
            } else {
                binding.textBenefit1.visibility = android.view.View.GONE
                binding.textBenefit2.visibility = android.view.View.GONE
            }

            binding.btnStartExercise.setOnClickListener {
                onExerciseClick(exercise)
            }
        }
    }
}
