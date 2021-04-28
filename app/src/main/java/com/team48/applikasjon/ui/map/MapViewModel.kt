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

class MapViewModel(val repository: Repository) : ViewModel() {

    //private val weatherList = repository.getWeather()

    /*
    val weatherList = liveData {
        emit(repository.getWeather())
    }
     */


    var dataReady = false

    val weatherList: LiveData<MutableList<VectorDataset>> = liveData {

        Log.d("dataready livedata, pre call", dataReady.toString())
        val data = repository.getWeather()
        emit(data)

        Log.d("dataready livedata, post call", dataReady.toString())

        dataReady = true
    }

    /*
    val _weatherList: MutableList<VectorDataset> = weatherList.value!!.apply {
       _weatherList = weatherList.value!!
    }
     */

    // TODO: Fjernes seinere hvis det ikke trengs
    /*
    // LiveData handling of TileSet
    private val _tileSet = MutableLiveData<TileSet>().apply {
        value = getTileSet(airTempList)
    }
    val tileSet: LiveData<TileSet> = _tileSet
     */


    fun getAirTempURL(): String { return weatherList.value!![2].url!! }

    // Gets ID from name attribute in vector dataset
    fun getIDfromURL(url: String): String {
        return url.substringAfterLast("/")
    }

    // Setting layer properties
    fun setLayerProperties(fillLayer: Layer, weatherType: String) {

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