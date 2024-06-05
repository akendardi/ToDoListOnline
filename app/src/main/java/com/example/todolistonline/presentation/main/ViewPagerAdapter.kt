package com.example.todolistonline.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.todolistonline.presentation.main.fragments.TodayFragment
import com.example.todolistonline.presentation.main.fragments.TomorrowFragment
import javax.inject.Inject

class ViewPagerAdapter @Inject constructor(
    fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFragment()
            1 -> TomorrowFragment()
            else -> throw RuntimeException("Unknown fragment position")
        }
    }
}