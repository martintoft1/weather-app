package com.team48.applikasjon.data.api

import android.util.Log
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

    // Felles liste for alle værtyper, 0 = clouds, 1 = precipitation, 2 = airTemp
    var weatherList: List<VectorDataset> = emptyList()

    init {

        // Oppdaterer weatherList ved API-kall
        requestVectorDatasets(metUri)
    }

    // Grensesnitt for viewModels for å få tak i weatherList
    fun getWeather(): List<VectorDataset> = weatherList

    // Henter hele datasettet til met og gjør om til liste over objekter med link til vektordata som attributt
    private fun requestVectorDatasets(vectorDataUri: String) {

        var vectorDatasets: List<VectorDataset>

        CoroutineScope(Dispatchers.IO).launch {
            vectorDatasets = gson.fromJson(Fuel.get(vectorDataUri).awaitString(), Array<VectorDataset>::class.java).toList()
            weatherList = updateWeatherList(vectorDatasets)
        }
    }

    // Finne nyeste data på hver værtype og legge i liste
    private fun updateWeatherList(vectorDataSets: List<VectorDataset>): MutableList<VectorDataset> {

        // TODO: Fikse algoritme mtp. airTemp

        val weatherDataset = mutableListOf<VectorDataset>()

        var name: String

        var updateAirTemp = true
        var updateClouds = true
        var updatePrecipitation = true

        var airTempIndex = 0
        var cloudsIndex = 0
        var precipitationIndex = 0

        for (i in vectorDataSets.indices.reversed()) {

            if (vectorDataSets[i].name != null) {

                name = vectorDataSets[i].name!!

                when {
                    (name.contains("precipitation") && updatePrecipitation) -> {
                        precipitationIndex = i
                        updatePrecipitation = false
                    }
                    (name.contains("cloud") && updateClouds) -> {
                        cloudsIndex = i
                        updateClouds = false
                    }
                    (name.contains("air_temperature_2m") && updateAirTemp) -> {
                        airTempIndex = i
                        updateAirTemp = false
                    }
                    else -> {
                        //Log.d("name when else", name)
                    }
                } // TODO: Utbedre else
            }

            // Returner når værtypene er oppdatert
            if (!updatePrecipitation && !updateClouds && !updateAirTemp) {
                weatherDataset.add(0, vectorDataSets[cloudsIndex])
                weatherDataset.add(1, vectorDataSets[precipitationIndex])
                weatherDataset.add(2, vectorDataSets[airTempIndex])

                break
            }
        }
        return weatherDataset
    }
}