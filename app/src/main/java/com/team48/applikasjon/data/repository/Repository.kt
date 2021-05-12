package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.database.LocationsDatabase
import com.team48.applikasjon.data.models.Location

class Repository (val context: Context){

    private val apiHelper = ApiHelper(ApiServiceImpl())

    private val database = LocationsDatabase.getDatabase(context)

    fun getWeather() = apiHelper.getWeather()

    suspend fun addLocation(location: Location) {
        database.locationDao().addLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        database.locationDao().deleteLocation(location)
    }

    fun getAllLocations() : LiveData<MutableList<Location>> {
        return database.locationDao().getLocations()
    }

    fun getCount() : LiveData<Int> {
        return database.locationDao().getCount()
    }
}