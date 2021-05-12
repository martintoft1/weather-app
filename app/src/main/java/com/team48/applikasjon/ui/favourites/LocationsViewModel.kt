package com.team48.applikasjon.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team48.applikasjon.data.models.DatabaseLocation
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.launch

class LocationsViewModel(private val repository: Repository) : ViewModel() {

    var locations = mutableListOf<DatabaseLocation>() /* Store locations for easy access during runtime */

    fun getAllLocations() : LiveData<MutableList<DatabaseLocation>> {
        return repository.getAllLocations()
    }

    fun deleteLocation(position: Int) {
        viewModelScope.launch { repository.deleteLocation(locations[position]) }
        locations.removeAt(position)
    }

    fun getCount() : LiveData<Int> {
        return repository.getCount()
    }
}


