package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapViewModel(val repository: Repository) : ViewModel() {

    // Felles liste for alle værtyper, 0 = precipitation, 1 = clouds, 2 = airTemp
    private lateinit var weatherList: MutableList<VectorDataset>

    // Forhindrer innlasting av layers før data er tilgjengelig
    var dataReady = false

    // Kalles på i MapFragment
    fun updateWeather() {

        // TODO: Fjerning av runBlocking

        runBlocking {
            delay(5000)
            weatherList = repository.getWeather()
            dataReady = true
        }
    }

    // Gets ID from name attribute in vector dataset
    fun getIDfromURL(url: String): String {
        return url.substringAfterLast("/")
    }

    // Kalles på av MapFragment, posistion iht. spinner
    // Clouds: 0, Precipitiation: 1, airTemp = 2
    fun getWeatherTypeURL(position: Int): String {
        return weatherList[position].url!!
    }

    // Setting layer properties
    fun setLayerProperties(fillLayer: Layer, weatherType: Int) {

        // weatherType = 0: Clouds
        // = 1: Precipitiation
        // = 2: AirTemp

        // TODO: Create better presentation based on weather type
        fillLayer.setProperties(
            PropertyFactory.fillOpacity(0.4F),
            PropertyFactory.fillColor(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(-20f, Expression.color(Color.RED)),
                    Expression.stop(0f, Expression.color(Color.WHITE)),
                    Expression.stop(20f, Expression.color(Color.BLUE))
                )
            )
        )
    }

    // Creating start position over Norway
    // TODO: Access user location as start position
    fun getCamStartPos(): CameraPosition {
        return CameraPosition.Builder()
                .target(LatLng(62.0, 16.0, 1.0))
                .zoom(3.0)
                .tilt(0.0)
                .build()
    }

    // Function onCleared is called prior to ViewModel destruction
    // (fragment detached / activity finished)
    override fun onCleared() {
        super.onCleared()
        Log.i("MapViewModel", "MapViewModel destroyed!")
    }



}