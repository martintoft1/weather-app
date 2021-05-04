package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
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
import com.team48.applikasjon.utils.SafeClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.stream.IntStream.range

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

        // Opacity settes til 0 initielt

        // TODO: Create better presentation based on weather type
        when (weatherType) {
            0 -> {
                fillLayer.setProperties( // clouds
                    fillOpacity(0.0F),
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
                    fillOpacity(0.0F),
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
                    fillOpacity(0.0F),
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

    // Kalles på av async i MapFragment. Oppretter array med verdier for værdata fra gitt punkt
    // dataArr[3]:
    // 0: clouds (enhet skydekke)
    // 1: rain (mm)
    // 2: temp (celcius)
    fun getWeatherFrom(map: MapboxMap, point: LatLng, bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>) {


        // Convert LatLng coordinates to screen pixel and only query the rendered features.
        val pixel = map.projection.toScreenLocation(point)
        var dataArr = arrayOfNulls<String>(3)

        if (map.queryRenderedFeatures(pixel, "layer1","layer2","layer3").size > 0) {
            for (i in dataArr.indices) {
                val jsonData = map.queryRenderedFeatures(pixel, "layer${i+1}")
                if (jsonData.size > 0) {
                    dataArr[i] = jsonData[0].properties()!!["value"].toString()
                }
            }
        } else {
            Log.d("getWeatherFrom()", "Trykk innenfor Norden!")
            return
        }

        Log.d("features", dataArr.contentToString())
        // Skyer, regn, temp


        // Vise bottom sheet
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {

            Log.d("onclick", "collapsing")
            bottomSheetBehavior.state =  BottomSheetBehavior.STATE_EXPANDED
        }
    }


    // Henter metadataURL fra weatherList basert på spinnerposisjon
    fun getLayerURL(position: Int): String {

        // Position nedskiftet med 1, ettersom spinner posision 0 indikerer noLayer
        return weatherList[position - 1].url!!
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
        for (i in range(1,4))
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
        setLayerProperties(fillLayer, position - 1)

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