package com.team48.applikasjon.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.locations.LocationsViewModel
import com.team48.applikasjon.ui.map.MapViewModel
import com.team48.applikasjon.ui.settings.SettingsViewModel

/* ViewModelFactory for opprettelse av alle ViewModels med samme Repository */
class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {

            // MapViewModel
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(repository) as T
            }

            // SettingsViewModel
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(repository) as T
            }

            // LocationsViewModel
            modelClass.isAssignableFrom(LocationsViewModel::class.java) -> {
                LocationsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}