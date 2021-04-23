package com.team48.applikasjon.data.api

import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.models.VectorTile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Klarte ikke opprette instans av ApiService direkte, så kanskje er ikke denne klassen helt ubrukelig likevel? Idk

class ApiServiceImpl : MutableLiveData<ApiService>() {

    // URL til met værdata
    private val metUri = "https://test.openmaps.met.no/in2000/map/services"

    private val gson = Gson()

    private var airTemp = mutableListOf<VectorTile>()
    private var clouds = mutableListOf<VectorTile>()
    private var precipitation = mutableListOf<VectorTile>()
    private var pressure = mutableListOf<VectorTile>()

    // Initialiserer API-kall
    init {
        requestVectorDatasets(metUri)
    }

    // Grensesnitt for klassen
    fun getAirTemp() = airTemp
    fun getClouds() = clouds
    fun getPrecipitation() = precipitation
    fun getPressure() = pressure


    // Henter hele datasettet til met og gjør om til liste over objekter med link til vektordata som attributt
    private fun requestVectorDatasets(vectorDataUri: String) {
        var vectorDatasets: List<VectorDataset>

        CoroutineScope(Dispatchers.IO).launch {
            vectorDatasets = gson.fromJson(Fuel.get(vectorDataUri).awaitString(), Array<VectorDataset>::class.java).toList()
            requestVectorTiles(vectorDatasets)
        }
    }

    // Henter data fra hver enkelt vectortile
    private fun requestVectorTiles(vectorDatasets: List<VectorDataset>) {

        val vectorTiles = mutableListOf<VectorTile>()
        CoroutineScope(Dispatchers.IO).launch {
            for (vectorDataset in vectorDatasets) {
                vectorTiles.add(gson.fromJson(Fuel.get(vectorDataset.url.toString()).awaitString(), VectorTile::class.java))
            }

            sortVectorTiles(vectorTiles)
        }
    }

    // Sorterer vectortiles etter type værdata de tilbyr
    private fun sortVectorTiles(vectorTiles: List<VectorTile>) {
        for (vectorTile in vectorTiles) {
            val weatherType = vectorTile.description?.substringBefore('/')
            when (weatherType) {
                "air_temperature" -> airTemp.add(vectorTile)
                "cloud" -> clouds.add(vectorTile)
                "precipitation" -> precipitation.add(vectorTile)
                "pressure" -> pressure.add(vectorTile)
            }
        }
    }
}