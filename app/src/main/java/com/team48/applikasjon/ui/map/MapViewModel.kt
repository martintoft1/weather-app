package com.team48.applikasjon.ui.map

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapViewModel(private val repository: Repository) : ViewModel() {

    private val airTempList = MutableLiveData<Resource<List<VectorTile>>>()
    private val cloudList = MutableLiveData<Resource<List<VectorTile>>>()
    private val precipitationList = MutableLiveData<Resource<List<VectorTile>>>()
    private val pressureList = MutableLiveData<Resource<List<VectorTile>>>()

    private val compositeDisposable = CompositeDisposable()

    init {

        // Fikse init basert på apply, observe og livedata
        /*
        fetchWeatherType(airTempList)
        fetchWeatherType(cloudList)
        fetchWeatherType(precipitationList)
        fetchWeatherType(pressureList)
         */


        // Initializing MapBox instance
        //Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))
    }

    // Denne må fjernes, singles-implementasjon er ikke det vi bør gå for
    /*
    private fun fetchWeatherType(weatherType: MutableLiveData<Resource<List<VectorTile>>>) {

        // Originalt var det this.weatherType. Ikke sikkert dette funker?
        weatherType.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getAirTemp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ airTempList ->
                    weatherType.postValue(Resource.success(airTempList))
                }, {
                   weatherType.postValue(Resource.error("airTempList update error", null))
                })
        )
    }
     */

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