package com.example.habbittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habbittracker.databinding.FragmentOnboardingScreen2Binding

class OnboardingScreen2Fragment : Fragment() {

    private var _binding: FragmentOnboardingScreen2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingScreen2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnSkip.setOnClickListener {
            // Skip to main app
            (requireActivity() as com.example.habbittracker.MainActivity).completeOnboarding()
        }
        
        binding.btnNext.setOnClickListener {
            // Move to next screen
            (requireActivity() as com.example.habbittracker.MainActivity).nextOnboardingScreen()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
