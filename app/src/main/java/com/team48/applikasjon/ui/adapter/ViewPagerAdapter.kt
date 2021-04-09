package com.team48.applikasjon.ui.adapter

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter {
    class ViewPagerAdapter(@NonNull fragmentManager: FragmentManager?, b: Lifecycle?) :
        FragmentStateAdapter(fragmentManager!!, b!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()

        fun addFragment(fragment: Fragment) {
            mFragmentList.add(fragment)
        }

        @NonNull
        override fun createFragment(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getItemCount(): Int {
            return mFragmentList.size
        }
    }
}