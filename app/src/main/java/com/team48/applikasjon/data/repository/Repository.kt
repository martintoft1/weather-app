package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.database.LocationsDatabase
import com.team48.applikasjon.data.models.DatabaseLocation

class Repository (context: Context){

    private val apiHelper = ApiHelper(ApiServiceImpl())

    private val database = LocationsDatabase.getDatabase(context)

    fun getWeather() = apiHelper.getWeather()

    suspend fun addLocation(databaseLocation: DatabaseLocation) {
        database.locationDao().addLocation(databaseLocation)
    }

    suspend fun deleteLocation(databaseLocation: DatabaseLocation) {
        database.locationDao().deleteLocation(databaseLocation)
    }

    fun getAllLocations() : LiveData<MutableList<DatabaseLocation>> {
        return database.locationDao().getLocations()
    }

    fun getCount() : LiveData<Int> {
        return database.locationDao().getCount()
    }

    suspend fun clearDatabase() {
        database.locationDao().clearDatabase()
    }
}