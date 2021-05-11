package com.team48.applikasjon.ui.favourites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Location
import com.team48.applikasjon.ui.favourites.adapters.LocationsAdapter
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.utils.LocationSwipeHandler


class LocationsFragment(val viewModelFactory: ViewModelFactory)
    : Fragment(), LocationsAdapter.OnLocationClickListener {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var locationsViewModel: LocationsViewModel
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var locations: MutableList<Location>


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favourites, container, false)
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerview()
    }


    private fun setupViewModel() {
        locationsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(LocationsViewModel::class.java)
    }


    private fun setupRecyclerview() {
        /* Initialize */
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        /* Get favourite locations from database */
        locationsViewModel.getAllLocations().observe(viewLifecycleOwner, Observer {
            locationsAdapter = LocationsAdapter(it as MutableList<Location>, this)
            recyclerView.adapter = locationsAdapter
            locationsViewModel.locations = it as MutableList<Location>

            /* Attach swipehandler to recyclerview */
            val locationSwipeHandler = LocationSwipeHandler(requireContext(), locationsViewModel)
            ItemTouchHelper(locationSwipeHandler).attachToRecyclerView(recyclerView)
        })
    }



    /* Recyclerview item onClick */
    override fun onLocationClick(position: Int, view: View) {
        println("Location clicked: pos $position")

        val isExpanded = locationsViewModel.locations[position].expanded
        val expandedView = view.findViewById<LinearLayout>(R.id.location_expanded)
        TransitionManager.beginDelayedTransition(view.findViewById(R.id.cv_location), AutoTransition());

        if (isExpanded) {
            expandedView.visibility = View.GONE
            locationsViewModel.locations[position].expanded = false
        }
        else {

            expandedView.visibility = View.VISIBLE
            locationsViewModel.locations[position].expanded = true
        }
    }

    /* Delete location onClick */
    override fun onLocationDeleteClick(position: Int) {
        locationsViewModel.deleteLocation(position)
    }

    /* Show location on map onClick */
    override fun onLocationMapClick(position: Int) {
        // TODO: Navigere til valgt lokasjon i mapfragment
    }
}
