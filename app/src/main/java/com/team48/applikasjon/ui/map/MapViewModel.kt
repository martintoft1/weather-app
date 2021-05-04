package com.team48.applikasjon.ui.map

import android.graphics.Color
import androidx.lifecycle.*
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapViewModel(val repository: Repository) : ViewModel() {

    // Felles liste for alle værtyper, 0 = precipitation, 1 = clouds, 2 = airTemp
    private lateinit var weatherList: MutableList<VectorDataset>

    // Forhindrer innlasting av layers før data er tilgjengelig
    var dataReady = false

    // Hashmap som holder på opprettede layers
    private var layerHashMap: HashMap<Int, Layer> = hashMapOf()

    // Henter oppdatert data fra repository
    fun updateWeather() {

        // TODO: Fjerning av runBlocking

        runBlocking {
            delay(5000)
            weatherList = repository.getWeather()
            dataReady = true
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

        // TODO: Create better presentation based on weather type
        when (weatherType) {
            0 -> {
                fillLayer.setProperties( // clouds
                    fillOpacity(0.4F),
                    fillColor(interpolate(
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
                    fillOpacity(0.4F),
                    fillColor(interpolate(
                        linear(), get("value"),
                        stop(0, color(Color.TRANSPARENT)),
                        stop(1, color(Color.BLUE))
                    )
                    )
                )

            }
            2 -> {
                fillLayer.setProperties( // airTemp
                    fillOpacity(0.4F),
                    fillColor(interpolate(
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

    // Velger et nytt filter, oppretter det hvis det ikke eksisterer
    fun chooseLayer(style: Style, position: Int) {

        // Hvis layer allerede er opprettet
        if (layerHashMap.containsKey(position)) {

            // Skjul eventuelle nåværende layers og vis ønsket layer
            hideAllLayers()
            layerHashMap[position]!!.setProperties(PropertyFactory.visibility(Property.VISIBLE))
        }

        // Om layer ikke tidligere er opprettet
        else addNewLayer(style, position)
    }

    // Henter metadataURL fra weatherList basert på spinnerposisjon
    fun getLayerURL(position: Int): String {

        // Position nedskiftet med 1, ettersom spinner posision 0 indikerer noLayer
        return weatherList[position - 1].url!!
    }

    // Skjuler alle layers ved å sette opacity til 0
    fun hideAllLayers() {

        // TODO: Endre til opacity-justering i stedet for visibility NONE
        for ((index, layer) in layerHashMap)
            layer.setProperties(PropertyFactory.visibility(Property.NONE))
    }

    // Legger til data i style for å generere et nytt layer
    fun addNewLayer(style: Style, position: Int) {

        val sourceId = "vectorsource$position"
        val layerId = "layer$position"
        val layerURL = getLayerURL(position)

        // Adding source to style
        val vectorSource = VectorSource(sourceId, layerURL)
        style.addSource(vectorSource)

        // Creating layer
        val fillLayer = FillLayer(layerId, sourceId)

        // Setting layer properties
        setLayerProperties(fillLayer, position - 1)

        // Adding sourcelayer ID
        fillLayer.sourceLayer = getIDfromURL(layerURL)

        // Skjul nåværende layers
        hideAllLayers()

        // Adding layer to style
        style.addLayer(fillLayer)

        // Legger til referanse til layer i hashmap, oppdaterer index
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