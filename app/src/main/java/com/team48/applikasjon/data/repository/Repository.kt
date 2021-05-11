package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.team48.applikasjon.R
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.database.LocationsDatabase
import com.team48.applikasjon.data.models.Location

class Repository (val context: Context){

    private val apiHelper = ApiHelper(ApiServiceImpl())
    private val defaultStyle: String = R.string.mapStyleLight.toString()
    val modeStyle: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val database = LocationsDatabase.getDatabase(context)
    val locations = database.locationDao().getLocations()


    fun getWeather() = apiHelper.getWeather()

    /*
    fun setCustomMapStyle(style: String) {
        customMapStyle = style
    }
    fun getCustomMapStyle() = customMapStyle

     */

    suspend fun addLocation(location: Location) {
        database.locationDao().addLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        database.locationDao().deleteLocation(location)
    }

    fun getAllLocations() : LiveData<List<Location>> {
        return database.locationDao().getLocations()
    }

    fun getCount() : LiveData<Int> {
        return database.locationDao().getCount()
    }
}