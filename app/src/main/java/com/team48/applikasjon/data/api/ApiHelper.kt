package com.team48.applikasjon.data.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.mapbox.mapboxsdk.maps.Style
import com.team48.applikasjon.VectorDataset
import com.team48.applikasjon.VectorTile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiHelper() {

    // URL til met værdata
    private val metUri = "https://test.openmaps.met.no/in2000/map/services"

    // URL til egen designet mapbox stil
    private val mapStyleUri = "mapbox://styles/wipi-48/cknilzu8v0eok17nyuucg7mfb"

    private val gson = Gson()
    private lateinit var mapStyle: Style.Builder

    private var airTemp = mutableListOf<VectorTile>()
    private var clouds = mutableListOf<VectorTile>()
    private var precipitation = mutableListOf<VectorTile>()
    private var pressure = mutableListOf<VectorTile>()

    private fun requestMapStyle(uri: String) {
        mapStyle = Style.Builder().fromUri(uri)
    }

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

        var vectorTiles = mutableListOf<VectorTile>()
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

    // Grensesnitt for klassen
    fun getMapStyle() = mapStyle
    fun getAirTemp() = airTemp
    fun getClouds() = clouds
    fun getPrecipitation() = precipitation
    fun getPressure() = pressure

    // Initialiserer API-kall
    init {
        requestMapStyle(mapStyleUri)
        requestVectorDatasets(metUri)
    }
}