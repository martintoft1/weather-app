package com.team48.applikasjon.ui.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/* Adapter som tar vare på og lagrer states til hovedfragments */
class FragmentContainerAdapter(fragmentManager: FragmentManager?, b: Lifecycle?) :
        FragmentStateAdapter(fragmentManager!!, b!!) {

    // Liste over alle fragmenter
    private val fragments = mutableListOf<Fragment>()

    // Legger til fragment
    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    // Oppretter fragmenter
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    // Henter antall fragmenter
    override fun getItemCount(): Int {
        return fragments.size
    }
}
