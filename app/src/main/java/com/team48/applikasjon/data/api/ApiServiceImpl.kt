package com.team48.applikasjon.data.api

import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.team48.applikasjon.data.models.VectorDataset
import kotlinx.coroutines.*
import java.util.*

class ApiServiceImpl : MutableLiveData<ApiService>() {

    // URL til MET værdata og Gson()-objekt
    private val metUri = "https://test.openmaps.met.no/in2000/map/services"
    private val gson = Gson()

    /* Felles liste for alle værtyper, 0 = clouds, 1 = precipitation, 2 = airTemp
       Holder bare på nyeste data */
    var weatherList: List<VectorDataset> = emptyList()

    // API-kall gjøres ved oppstart
    init {

        // Oppdaterer weatherList med API-kall
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

    // Henter ID fra URL-medlemmet i VectorDataset
    private fun getIDfromURL(url: String): String {
        return url.substringAfterLast("/")
    }

    // Finne nyeste data på hver værtype og legge i liste
    private fun updateWeatherList(vectorDatasets: List<VectorDataset>): MutableList<VectorDataset> {

        /* Ettersom MET ikke er konsekvente på ID og datostempling blir vi nødt
         * å iterere gjennom alle dataene. Det er forventet med update på API
         * "plutselig", så vi må forsøke å beskytte oss mot dette også */

        val weatherDataset = mutableListOf<VectorDataset>()
        var name: String
        var airTempIndex = -1
        var airTempID = -1
        var cloudIndex = -1
        var cloudID = -1
        var preIndex = -1
        var preID = -1
        var newID: Int

        // Itererer gjennom indeks reverset, da det vil kreve minst endringer per iterasjon
        for (i in vectorDatasets.indices.reversed()) {

            name = vectorDatasets[i].name!!
            newID = getIDfromURL(vectorDatasets[i].url!!).toInt()

            // Lagrer indeks for hver av værtypene med høyeste ID-nummer
            when {
                (name.contains("precipitation")) -> {
                    if (newID > preID) {
                        preIndex = i
                        preID = newID
                    }
                }
                (name.contains("cloud")) -> {
                    if (newID > cloudID) {
                        cloudIndex = i
                        cloudID = newID
                    }
                }
                (name.contains("air_temperature_2m")) -> {
                    if (newID > airTempID) {
                        airTempIndex = i
                        airTempID = newID
                    }
                }
                else -> { // Do nothing
                }
            }
        }

        // Oppdaterer liste med ferskeste værdata
        weatherDataset.add(0, vectorDatasets[cloudIndex])
        weatherDataset.add(1, vectorDatasets[preIndex])
        weatherDataset.add(2, vectorDatasets[airTempIndex])

        return weatherDataset
    }
}