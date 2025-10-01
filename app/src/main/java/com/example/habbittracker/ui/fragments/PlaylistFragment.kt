package com.example.habbittracker.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.R
import com.example.habbittracker.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.title = "My playlist"

        adapter = PlaylistAdapter(
            onPlayClick = { url -> playUrl(url) },
            onPauseClick = { pausePlayback() }
        )
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSongs.adapter = adapter

        // Sample playlist items (royalty-free/public domain demo URLs)
        val items = listOf(
            PlaylistItem("Calm Piano", "Artist A", "https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav"),
            PlaylistItem("Energy Beat", "Artist B", "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav"),
            PlaylistItem("Lo-fi Loop", "Artist C", "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav"),
            PlaylistItem("Acoustic Breeze", "Artist D", "https://www2.cs.uic.edu/~i101/SoundFiles/ImperialMarch60.wav"),
            PlaylistItem("Deep Focus", "Artist E", "https://www2.cs.uic.edu/~i101/SoundFiles/Front_Center.wav"),
            PlaylistItem("Sunset Chill", "Artist F", "https://www2.cs.uic.edu/~i101/SoundFiles/PinkPanther60.wav"),
            PlaylistItem("Morning Walk", "Artist G", "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav"),
            PlaylistItem("Raindrop Study", "Artist H", "https://www2.cs.uic.edu/~i101/SoundFiles/ClassicRock1-4.wav")
        )
        adapter.submitList(items)
    }

    private fun playUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { it.start() }
            setOnCompletionListener { /* keep stopped */ }
            prepareAsync()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}

data class PlaylistItem(
    val title: String,
    val artist: String,
    val url: String
)

class PlaylistAdapter(
    private var items: List<PlaylistItem> = emptyList(),
    private val onPlayClick: (String) -> Unit,
    private val onPauseClick: () -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<PlaylistAdapter.VH>() {

    class VH(val binding: com.example.habbittracker.databinding.ItemPlaylistBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = com.example.habbittracker.databinding.ItemPlaylistBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.textTitle.text = item.title
        holder.binding.textArtist.text = item.artist
        holder.binding.btnPlay.setOnClickListener { onPlayClick(item.url) }
        holder.binding.btnPause.setOnClickListener { onPauseClick() }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<PlaylistItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}


