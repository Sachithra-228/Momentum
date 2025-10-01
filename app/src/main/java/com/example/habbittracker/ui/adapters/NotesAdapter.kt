package com.example.habbittracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.data.ReadingNote
import com.example.habbittracker.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private var notes: List<ReadingNote>
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    fun submitList(newNotes: List<ReadingNote>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: ReadingNote) {
            binding.textNote.text = note.text
            val df = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            binding.textTime.text = df.format(Date(note.timestamp))
        }
    }
}


