package com.example.habbittracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.R
import com.example.habbittracker.data.Habit
import com.example.habbittracker.databinding.ItemHabitBinding

/**
 * Adapter for displaying habits in a RecyclerView
 */
class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit,
    private val onHabitIncrement: (Habit) -> Unit,
    private val onHabitDecrement: (Habit) -> Unit,
    private val onHabitToggle: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
    
    override fun getItemCount(): Int = habits.size
    
    inner class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(habit: Habit) {
            binding.apply {
                textHabitName.text = habit.name
                textHabitType.text = if (habit.type == "single") "Single" else "Countable"
                
                // Update progress
                val progressPercentage = habit.getProgressPercentage()
                progressBar.progress = progressPercentage.toInt()
                
                // Update progress text
                if (habit.type == "single") {
                    textProgress.text = if (habit.isCompletedToday()) "Completed" else "Not completed"
                } else {
                    textProgress.text = "${habit.currentCountToday}/${habit.targetCount}"
                }
                
                // Show/hide increment/decrement buttons for countable habits
                if (habit.type == "countable") {
                    btnIncrement.visibility = View.VISIBLE
                    btnDecrement.visibility = View.VISIBLE
                    btnToggle.visibility = View.GONE
                    
                    btnIncrement.isEnabled = habit.currentCountToday < habit.targetCount
                    btnDecrement.isEnabled = habit.currentCountToday > 0
                } else {
                    btnIncrement.visibility = View.GONE
                    btnDecrement.visibility = View.GONE
                    btnToggle.visibility = View.VISIBLE
                    
                    btnToggle.text = if (habit.isCompletedToday()) "Undo" else "Complete"
                }
                
                // Set click listeners
                root.setOnClickListener { onHabitClick(habit) }
                btnIncrement.setOnClickListener { onHabitIncrement(habit) }
                btnDecrement.setOnClickListener { onHabitDecrement(habit) }
                btnToggle.setOnClickListener { onHabitToggle(habit) }
                
                // Overflow menu
                btnOverflow.setOnClickListener { showOverflowMenu(habit) }
            }
        }
        
        private fun showOverflowMenu(habit: Habit) {
            val context = binding.root.context
            val popup = android.widget.PopupMenu(context, binding.btnOverflow)
            popup.menuInflater.inflate(R.menu.habit_overflow_menu, popup.menu)
            
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onHabitClick(habit)
                        true
                    }
                    R.id.action_delete -> {
                        onHabitDelete(habit)
                        true
                    }
                    else -> false
                }
            }
            
            popup.show()
        }
    }
}
