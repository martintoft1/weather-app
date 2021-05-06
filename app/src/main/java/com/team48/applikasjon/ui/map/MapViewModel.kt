package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.google.android.material.internal.ContextUtils.getActivity
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.MainActivity
import kotlinx.coroutines.*
import java.util.Collections.emptyList
import java.util.stream.IntStream.range

class MapViewModel(val repository: Repository) : ViewModel() {

    // Felles liste for alle værtyper, 0 = precipitation, 1 = clouds, 2 = airTemp
    var liveWeather: List<VectorDataset> = emptyList()

    // Forhindrer vising av layers før de er initialisert
    var dataReady = false

    // Hashmap som holder på opprettede layers
    private var layerHashMap: HashMap<Int, Layer> = hashMapOf()

    // Opprettelse av layers basert på API-data
    fun updateWeather(style: Style) {

        CoroutineScope(Dispatchers.IO).launch {
            while (liveWeather.isEmpty()) {
                delay(500)
                liveWeather = repository.getWeather()
            }
            withContext(Dispatchers.Main) {
                addAllLayers(style)
                dataReady = true
            }
        }
    }

    // Parser ut layerID fra metadataURL
    fun getIDfromURL(url: String): String {
        return url.substringAfterLast("/")
    }

    // Justering av hvordan layers presenteres på skjerm
    fun setLayerProperties(fillLayer: Layer, weatherType: Int) {

        /*
        WeatherType:
        0: Clouds
        1: Precipitiation
        2: AirTemp
        */
        // Opacity settes til 0 initielt

        // TODO: Create better presentation based on weather type
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
                    fillColor(
                        interpolate(
                            linear(), get("value"),
                            stop(0, color(Color.TRANSPARENT)),
                            stop(1, color(Color.BLUE))
                        )
                    )
                )

            }
            2 -> {
                fillLayer.setProperties( // airTemp
                    fillOpacity(0.0F),
                    fillColor(
                        interpolate(
                            exponential(2F), get("value"),
                            stop(-10, color(Color.BLUE)),
                            stop(0, color(Color.WHITE)),
                            stop(10, color(Color.YELLOW)),
                            stop(20, color(Color.RED))
                        )
                    )
                )
            }
        }
    }

    fun getWeatherFrom(map: MapboxMap, point: LatLng) {
        // Convert LatLng coordinates to screen pixel and only query the rendered features.
        val pixel = map.projection.toScreenLocation(point)
        var dataArr = arrayOfNulls<Float>(3)

        if (map.queryRenderedFeatures(pixel, "layer0", "layer1", "layer2").size > 0) {
            for (i in dataArr.indices) {
                val jsonData = map.queryRenderedFeatures(pixel, "layer${i}")
                if (jsonData.size > 0) {
                    dataArr[i] = jsonData[0].properties()!!["value"].toString().toFloat()
                } else {
                    dataArr[i] = 0F
                }
            }
        } else {
            Log.d("getWeatherFrom()", "Trykk innenfor Norden!")
            return
        }

        // TODO: opprett xml eller boks til å displaye data
        Log.d("features", dataArr.contentToString())
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
    fun chooseLayer(style: Style, position: Int) {

        // Skjul eventuelle nåværende layers og vis ønsket layer
        hideAllLayers()
        showLayer(layerHashMap[position]!!)
    }

    // Funksjon som legger til alle filtere basert på en ID
    fun addAllLayers(style: Style) {
        for (i in range(0, 3))
            addNewLayer(style, i)
    }

    // Legger til data i style for å generere et nytt layer
    fun addNewLayer(style: Style, position: Int) {

        val sourceId = "vectorsource$position"
        val layerId = "layer$position"
        val layerURL = getLayerURL(position)

        // Legger til ny source i style
        val vectorSource = VectorSource(sourceId, layerURL)
        style.addSource(vectorSource)

        // Oppretter layer og setter egenskaper
        val fillLayer = FillLayer(layerId, sourceId)
        setLayerProperties(fillLayer, position)

        // Adding sourcelayer ID
        fillLayer.sourceLayer = getIDfromURL(layerURL)

        // Adding layer to style
        style.addLayer(fillLayer)

        // Legger til referanse til layer i HashMap
        layerHashMap[position] = fillLayer

    }

    // Setter startposisjon til over Norge
    // TODO: Begynne ved brukers posisjon i stedet?
    fun getCamStartPos(): CameraPosition {
        return CameraPosition.Builder()
            .target(LatLng(62.0, 16.0, 1.0))
            .zoom(3.0)
            .tilt(0.0)
            .build()
    }
}