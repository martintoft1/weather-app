package com.team48.applikasjon.ui.map

import android.graphics.Color
import androidx.lifecycle.*
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.*
import java.util.Collections.emptyList
import java.util.stream.IntStream.range

/* ViewModel relatert til MapFragment */
class MapViewModel(val repository: Repository) : ViewModel() {

    // Felles liste for alle værtyper, 0 = precipitation, 1 = clouds, 2 = airTemp
    var liveWeather: List<VectorDataset> = emptyList()

    // Forhindrer vising av layers før de er initialisert
    var dataReady = false

    // Hashmap som holder på opprettede layers
    private var layerHashMap: HashMap<Int, Layer> = hashMapOf()

    // Returnerer default/light stil-referanse
    fun getDefaultStyleResource(): Int {
        return R.string.mapStyleLight
    }

    // Returnerer dark-mode stil-referanse
    fun getDarkModeStyleResource(): Int {
       return R.string.mapStyleDark
    }

    // Opprettelse av layers basert på API-data
    fun updateWeather(style: Style) {

        // LiveData er nedprioritert da vi bare har API-data fra 8. mars 2021 for case 4
        CoroutineScope(Dispatchers.IO).launch {
            while (liveWeather.isEmpty()) {
                delay(500)
                liveWeather = repository.getWeather()
            }
            withContext(Dispatchers.Main) {

                // Layers legges til med default presentasjon (light mode)
                addAllLayers(style, 0)
                dataReady = true
            }
        }
    }

    // Parser ut layerID fra metadataURL
    fun getIDfromURL(url: String): String {
        return url.substringAfterLast("/")
    }

    // Henter metadataURL fra weatherList basert på spinnerposisjon
    fun getLayerURL(position: Int): String {

        // Position nedskiftet med 1, ettersom spinner posision 0 indikerer noLayer
        return liveWeather[position].url!!
    }

    // Skjuler ett layer
    fun hideLayer(layer: Layer) {
        layer.setProperties(fillOpacity(0.0f))
    }

    // Skjuler alle layers
    fun hideAllLayers() {
        for ((index, layer) in layerHashMap)
            hideLayer(layer)
    }

    // Viser et layer ved å sette opacity > 0.0
    fun showLayer(layer: Layer) {
        layer.setProperties(fillOpacity(0.4f))
    }

    // Velger et nytt filter, skjuler forrige
    fun chooseLayer(position: Int) {

        // Skjul eventuelle nåværende layers og vis ønsket layer
        hideAllLayers()
        showLayer(layerHashMap[position]!!)
    }

    // Funksjon som legger til alle filtere basert på en ID
    fun addAllLayers(style: Style, visualMode: Int) {
        for (i in range(0, 3))
            addNewLayer(style, i, visualMode)
    }

    // Legger til data i style for å generere et nytt layer
    fun addNewLayer(style: Style, position: Int, visualMode: Int) {

        val sourceId = "vectorsource$position"
        val layerId = "layer$position"
        val layerURL = getLayerURL(position)

        // Legger til ny source i style
        val vectorSource = VectorSource(sourceId, layerURL)
        style.addSource(vectorSource)

        // Oppretter layer og setter egenskaper
        val fillLayer = FillLayer(layerId, sourceId)
        setLayerProperties(fillLayer, position, visualMode)

        // Adding sourcelayer ID
        fillLayer.sourceLayer = getIDfromURL(layerURL)

        // Adding layer to style
        style.addLayer(fillLayer)

        // Legger til referanse til layer i HashMap
        layerHashMap[position] = fillLayer

    }

    // Setter startposisjon til over Norge
    fun getCamNorwayPos(): CameraPosition {
        return CameraPosition.Builder()
            .target(LatLng(62.0, 16.0, 1.0))
            .zoom(3.0)
            .tilt(0.0)
            .build()
    }

    // Justering av hvordan layers presenteres på skjerm
    fun setLayerProperties(fillLayer: Layer, weatherType: Int, visualMode: Int) {

        /* WeatherType:
         * 0: Clouds
         * 1: Precipitiation
         * 2: AirTemp
         * Opacity settes til 0 initielt */

        when (weatherType) {
            0 -> {
                fillLayer.setProperties( // clouds
                        fillOpacity(0.0F),
                        fillColor(
                                interpolate(
                                        exponential(1F), get("value"),
                                        stop(0, color(Color.TRANSPARENT)),
                                        stop(33, color(Color.LTGRAY)),
                                        stop(100, color(Color.DKGRAY)),
                                )
                        )
                )
            }
            1 -> {
                fillLayer.setProperties( // percipation
                        fillOpacity(0.0F),
                        fillColor(interpolate(
                            linear(),
                            get("value"),
                            stop(0, color(Color.TRANSPARENT)),
                            stop(1, rgb(0, 255, 255)),
                            stop(2, rgb(0, 255, 0)),
                            stop(3, rgb(255, 255, 0)),
                            stop(4, rgb(255, 127, 0)),
                            stop(5, rgb(255, 0, 0)),
                        ))
                )
            }
            2 -> {
                fillLayer.setProperties( // airTemp
                        fillOpacity(0.0F),
                        fillColor(
                                interpolate(
                                        linear(), get("value"),
                                        stop(-10, rgb(255, 0, 255)),
                                        stop(-5, rgb(0, 0, 255)),
                                        stop(0, rgb(0, 255, 255)),
                                        stop(5, rgb(0, 255, 0)),
                                        stop(10, rgb(255, 255, 0)),
                                        stop(15, rgb(255, 127, 0)),
                                        stop(20, rgb(255, 0, 0)),
                                )
                        )
                )
            }
        }
    }
}