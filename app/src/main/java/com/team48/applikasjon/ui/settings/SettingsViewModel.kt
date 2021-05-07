package com.team48.applikasjon.ui.settings

import androidx.lifecycle.ViewModel
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository

class SettingsViewModel(val repository: Repository) : ViewModel() {

    fun enableDarkMode() {
        repository.customMapStyle.value = R.string.mapStyleDark.toString()
    }

    fun disableDarkMode() {
        repository.customMapStyle.value = R.string.mapStyleLight.toString()
    }
}