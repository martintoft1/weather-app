package com.team48.applikasjon.ui.favourites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Location
import com.team48.applikasjon.ui.main.ViewModelFactory

class LocationsFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var locationsViewModel: LocationsViewModel
    private lateinit var locationsAdapter: LocationsAdapter

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
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }

        locationsViewModel.getAllLocations().observe(viewLifecycleOwner, Observer {
            Log.i("setupViewModel", it.toString())
            recyclerView.adapter = LocationsAdapter(it)
        })
    }


    fun deleteLocation(location: Location) {
        locationsViewModel.deleteLocation(location)
    }
}
