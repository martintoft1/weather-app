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

    fun getAllLocations() : LiveData<List<Location>> {
        return repository.getAllLocations()
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch { repository.deleteLocation(location) }
    }
}


