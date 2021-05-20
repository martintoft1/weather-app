package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.database.LocationsDatabase
import com.team48.applikasjon.data.models.LocationModel

class Repository (context: Context){

    private val apiHelper = ApiHelper(ApiServiceImpl())

    private val database = LocationsDatabase.getDatabase(context)

    fun getWeather() = apiHelper.getWeather()

    suspend fun addLocation(locationModel: LocationModel) {
        database.locationDao().addLocation(locationModel)
    }

    suspend fun deleteLocation(locationModel: LocationModel) {
        database.locationDao().deleteLocation(locationModel)
    }

    fun getAllLocations() : LiveData<MutableList<LocationModel>> {
        return database.locationDao().getLocations()
    }

    fun getCount() : LiveData<Int> {
        return database.locationDao().getCount()
    }

    suspend fun clearDatabase() {
        database.locationDao().clearDatabase()
    }
}