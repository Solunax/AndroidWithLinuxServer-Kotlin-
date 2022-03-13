package com.example.kotlinserver.ViewPager

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(@NonNull fragmentActivity:FragmentActivity, fragments:ArrayList<Fragment>?):FragmentStateAdapter(fragmentActivity) {
    private var fragments = fragments

    override fun getItemCount(): Int {
        return fragments!!.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments!![position]
    }
}