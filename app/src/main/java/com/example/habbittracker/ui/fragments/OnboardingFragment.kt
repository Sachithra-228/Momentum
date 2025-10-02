package com.example.habbittracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.habbittracker.R
import com.example.habbittracker.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: OnboardingPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()
        setupPageIndicators()
    }

    private fun setupViewPager() {
        viewPagerAdapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        
        // Disable swipe gesture to control navigation manually
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setupPageIndicators() {
        val indicators = mutableListOf<ImageView>()
        
        // Create page indicators
        for (i in 0 until viewPagerAdapter.itemCount) {
            val indicator = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setImageResource(if (i == 0) R.drawable.indicator_selected else R.drawable.indicator_unselected)
                val margin = resources.getDimensionPixelSize(R.dimen.indicator_margin)
                (layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin, 0, margin, 0)
            }
            indicators.add(indicator)
            binding.pageIndicators.addView(indicator)
        }
        
        // Update indicators when page changes
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicators.forEachIndexed { index, indicator ->
                    indicator.setImageResource(
                        if (index == position) R.drawable.indicator_selected else R.drawable.indicator_unselected
                    )
                }
            }
        })
    }

    fun nextScreen() {
        if (binding.viewPager.currentItem < viewPagerAdapter.itemCount - 1) {
            binding.viewPager.currentItem = binding.viewPager.currentItem + 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class OnboardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingScreen1Fragment()
                1 -> OnboardingScreen2Fragment()
                2 -> OnboardingScreen3Fragment()
                else -> OnboardingScreen1Fragment()
            }
        }
    }
}
