package com.team48.applikasjon.ui.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/* Adapter som tar vare på og lagrer states til hovedfragments */

class FragmentContainerAdapter(fragmentManager: FragmentManager?, b: Lifecycle?) :
        FragmentStateAdapter(fragmentManager!!, b!!) {

    private val fragments = mutableListOf<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    fun getFragments() : MutableList<Fragment> {
        return fragments
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}
