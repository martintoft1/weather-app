package com.team48.applikasjon.data.api

import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.team48.applikasjon.data.models.VectorDataset
import kotlinx.coroutines.*
import java.util.*

class ApiServiceImpl : MutableLiveData<ApiService>() {

    // URL til met værdata
    private val metUri = "https://test.openmaps.met.no/in2000/map/services"
    private val gson = Gson()

    // Felles liste for alle værtyper
    // 0 = precipitation, 1 = clouds, 2 = airTemp
    private var weatherDataset = mutableListOf<VectorDataset>()

    // Initialiserer API-kall
    init {
        requestVectorDatasets(metUri)
    }

    fun getWeather() = weatherDataset

    fun refreshWeather() {
        requestVectorDatasets(metUri)
    }

    // Henter hele datasettet til met og gjør om til liste over objekter med link til vektordata som attributt
    private fun requestVectorDatasets(vectorDataUri: String) {
        var vectorDatasets: List<VectorDataset>

        CoroutineScope(Dispatchers.IO).launch {
            vectorDatasets = gson.fromJson(Fuel.get(vectorDataUri).awaitString(), Array<VectorDataset>::class.java).toList()

            withContext(Dispatchers.Main) {
                updateWeatherList(vectorDatasets)
            }
        }
    }

    // Finne nyeste data på hver værtype og legge i liste
    private fun updateWeatherList(vectorDataSets: List<VectorDataset>) {

        var name: String

        var airTempUpdated = false
        var cloudsUpdated = false
        var precipitationUpdated = false

        var airTempIndex = 0
        var cloudsIndex = 0
        var precipitationIndex = 0

        for (i in vectorDataSets.indices.reversed()) {

            if (vectorDataSets[i].name != null) {

                name = vectorDataSets[i].name!!

                when {
                    name.contains("precipitation") && !precipitationUpdated -> {
                        precipitationIndex = i
                        precipitationUpdated = true
                    }
                    name.contains("cloud") && !cloudsUpdated -> {
                        cloudsIndex = i
                        cloudsUpdated = true
                    }
                    name.contains("air_temperature_2m") && !airTempUpdated-> {
                        airTempIndex = i
                        airTempUpdated = true
                    }
                } // TODO: Trenger vi else?
            }

            // Returner når værtypene er oppdatert
            if (precipitationUpdated && cloudsUpdated && airTempUpdated) {
                weatherDataset.add(0, vectorDataSets[precipitationIndex])
                weatherDataset.add(1, vectorDataSets[cloudsIndex])
                weatherDataset.add(2, vectorDataSets[airTempIndex])
                return
            }
        }
    }
}