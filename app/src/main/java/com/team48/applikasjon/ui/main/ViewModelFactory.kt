package com.team48.applikasjon.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.favourites.LocationsViewModel
import com.team48.applikasjon.ui.map.MapViewModel
import com.team48.applikasjon.ui.settings.SettingsViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LocationsViewModel::class.java) -> {
                LocationsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}