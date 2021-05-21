package com.team48.applikasjon.ui.settings

import androidx.lifecycle.ViewModel
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository

/* ViewModel for SettingsFragment
* Holder bare på statiske referanser til Mapbox Studio-stiler */
class SettingsViewModel(val repository: Repository) : ViewModel() {

    fun enableDarkMode(): Int {
        return R.string.mapStyleDark
    }

    fun disableDarkMode(): Int {
        return R.string.mapStyleLight
    }
}