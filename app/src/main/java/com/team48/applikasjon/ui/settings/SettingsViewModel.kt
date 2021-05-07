package com.team48.applikasjon.ui.settings

import androidx.core.content.res.TypedArrayUtils.getString
import androidx.lifecycle.ViewModel
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository

class SettingsViewModel(val repository: Repository) : ViewModel() {

    fun enableDarkMode(): Int {
        return R.string.mapStyleDark
    }

    fun disableDarkMode(): Int {
        return R.string.mapStyleLight
    }

}