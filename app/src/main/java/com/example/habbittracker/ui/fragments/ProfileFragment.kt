package com.example.habbittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habbittracker.databinding.FragmentProfileBinding
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.data.Habit
import com.example.habbittracker.data.MoodEntry

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        loadProfile()
        loadStats()
        binding.btnSaveProfile.setOnClickListener { saveProfile() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProfile() {
        val name = preferencesHelper.getProfileName()
        val email = preferencesHelper.getProfileEmail()
        binding.textName.text = name
        binding.textEmail.text = email
        binding.inputName.setText(name)
        binding.inputEmail.setText(email)
    }

    private fun saveProfile() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
        if (name.isNotEmpty()) preferencesHelper.setProfileName(name)
        if (email.isNotEmpty()) preferencesHelper.setProfileEmail(email)
        loadProfile()
        android.widget.Toast.makeText(requireContext(), "Profile saved", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun loadStats() {
        val habits = preferencesHelper.getHabits()
        val completedToday = habits.count { it.isCompletedToday() }
        binding.textHabitsCompleted.text = "Habits completed today: $completedToday"

        val moodsLast7 = preferencesHelper.getMoodsForLastDays(7)
        binding.textMoodsLogged.text = "Moods logged this week: ${moodsLast7.size}"
    }
}


