package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.database.LocationsDatabase
import com.team48.applikasjon.data.models.LocationModel

/* Felles repository for alle ViewModels
 * Fungerer som et grenesnitt mellom API-klasser og ViewModels/Fragments, samt databasen
 */

class Repository (context: Context){

    // Opprettelse av ApiHelper-klassen
    private val apiHelper = ApiHelper(ApiServiceImpl())

    // Opprettelse av database-instans
    private val database = LocationsDatabase.getDatabase(context)

    // Grensesnitt mot API-klasser
    fun getWeather() = apiHelper.getWeather()

    // Legger til ny lokasjon i database
    suspend fun addLocation(locationModel: LocationModel) {
        database.locationDao().addLocation(locationModel)
    }

    // Fjerner lokasjon fra database
    suspend fun deleteLocation(locationModel: LocationModel) {
        database.locationDao().deleteLocation(locationModel)
    }

    // Henter alle lokasjoner i databasen
    fun getAllLocations() : LiveData<MutableList<LocationModel>> {
        return database.locationDao().getLocations()
    }

    // Sletter databasen fullstendig
    suspend fun clearDatabase() {
        database.locationDao().clearDatabase()
    }
}