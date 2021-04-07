package com.team48.applikasjon.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiService
import com.team48.applikasjon.data.repository.Repository

class MapViewModel : ViewModel() {

    init {
        Log.i("MapViewModel", "MapViewModel created!")
    }

    // Kalles før ViewModel blir destroyed (ved fragment detaching eller activity finished)
    override fun onCleared() {
        super.onCleared()
        Log.i("MapViewModel", "MapViewModel destroyed!")
    }
}