package com.team48.applikasjon.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team48.applikasjon.data.models.Location
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.launch

class LocationsViewModel(private val repository: Repository) : ViewModel() {

    //val locations = repository.getAllLocations()

    fun getAllLocations() : LiveData<List<Location>> {
        return repository.getAllLocations()
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch { repository.deleteLocation(location) }
    }
}