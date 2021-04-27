package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapViewModel(val repository: Repository) : ViewModel() {

    //private val airTempList = MutableLiveData<Resource<List<VectorTile>>>()
    private val cloudList = MutableLiveData<Resource<List<VectorTile>>>()
    private val precipitationList = MutableLiveData<Resource<List<VectorTile>>>()
    private val pressureList = MutableLiveData<Resource<List<VectorTile>>>()

    private val airTempList = repository.getAirTemp()


    fun getTileSet(airTempList: MutableList<VectorTile>): TileSet {

        // TODO: Håndtere på en annen måte en runBlocking
        runBlocking {
            delay(15000)
        }

        val index: Int = 0
        val tileSet = TileSet(airTempList[index].tilejson, airTempList[index].tiles?.get(0))

        return tileSet
    }

    // LiveData handling of TileSet
    private val _tileSet = MutableLiveData<TileSet>().apply {
        value = getTileSet(airTempList)
    }
    val tileSet: LiveData<TileSet> = _tileSet

    // Setting (fill)layer properties
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



    // Get tileID from VectorTile
    fun getTileID(tile: VectorTile): String {
        return tile.vector_layers!![0].id!!
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