package com.team48.applikasjon.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team48.applikasjon.data.models.Location
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.launch
import kotlin.math.exp

class LocationsViewModel(private val repository: Repository) : ViewModel() {

    var locations = mutableListOf<Location>() /* Store locations for easy access during runtime */

    fun getAllLocations() : LiveData<MutableList<Location>> {
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


