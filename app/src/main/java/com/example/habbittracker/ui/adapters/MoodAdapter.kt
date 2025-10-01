package com.example.habbittracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.data.MoodEntry
import com.example.habbittracker.databinding.ItemMoodBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying mood entries in a RecyclerView with date grouping
 */
class MoodAdapter(
    private val moods: List<MoodEntry>,
    private val onEdit: (MoodEntry) -> Unit,
    private val onDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_MOOD = 1
    }
    
    private val items = mutableListOf<Any>()
    
    init {
        updateItems()
    }
    
    private fun updateItems() {
        items.clear()
        
        if (moods.isEmpty()) return
        
        var currentDate: String? = null
        
        moods.forEach { mood ->
            val moodDate = mood.getDateString()
            
            if (currentDate != moodDate) {
                // Add date header
                items.add(moodDate)
                currentDate = moodDate
            }
            
            // Add mood entry
            items.add(mood)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> TYPE_HEADER
            is MoodEntry -> TYPE_MOOD
            else -> TYPE_MOOD
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_MOOD -> {
                val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MoodViewHolder(binding, onEdit, onDelete)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as String)
            is MoodViewHolder -> holder.bind(items[position] as MoodEntry)
        }
    }
    
    override fun getItemCount(): Int = items.size
    
    fun updateMoods(newMoods: List<MoodEntry>) {
        // Note: This method doesn't actually update the moods list since it's immutable
        // The calling fragment should create a new adapter instance with updated data
        updateItems()
        notifyDataSetChanged()
    }
    
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dateString: String) {
            val textView = itemView as android.widget.TextView
            
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
            val calendar = Calendar.getInstance()
            date?.let { calendar.time = it }
            
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            
            val displayText = when {
                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"
                
                calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
                
                else -> {
                    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                    format.format(calendar.time)
                }
            }
            
            textView.text = displayText
            textView.textSize = 16f
            textView.setPadding(16, 24, 16, 8)
        }
    }
    
    class MoodViewHolder(private val binding: ItemMoodBinding, private val onEdit: (MoodEntry) -> Unit, private val onDelete: (MoodEntry) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(mood: MoodEntry) {
            binding.apply {
                textEmoji.text = mood.emoji
                textTime.text = mood.getTimeString()
                
                if (mood.note.isNullOrBlank()) {
                    textNote.visibility = View.GONE
                } else {
                    textNote.visibility = View.VISIBLE
                    textNote.text = mood.note
                }

                btnEdit.setOnClickListener { onEdit(mood) }
                btnDelete.setOnClickListener { onDelete(mood) }
            }
        }
    }
}
