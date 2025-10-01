package com.example.habbittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.habbittracker.R
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.databinding.FragmentOnboardingSelectBinding

class OnboardingSelectFragment : Fragment() {

    private var _binding: FragmentOnboardingSelectBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper = (requireActivity() as com.example.habbittracker.MainActivity).getPreferencesHelper()

        val toggler = View.OnClickListener {
            val enabled = binding.checkMood.isChecked || binding.checkHabits.isChecked
            binding.btnContinue.isEnabled = enabled
        }
        binding.checkMood.setOnClickListener(toggler)
        binding.checkHabits.setOnClickListener(toggler)

        binding.btnContinue.setOnClickListener {
            // Require at least one selection
            if (binding.checkMood.isChecked || binding.checkHabits.isChecked) {
                preferencesHelper.setOnboardingDone(true)
                // Navigate to first selected tab
                val target = if (binding.checkMood.isChecked) R.id.nav_mood else R.id.nav_habits
                (requireActivity() as com.example.habbittracker.MainActivity).navigateToBottomTab(target)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


