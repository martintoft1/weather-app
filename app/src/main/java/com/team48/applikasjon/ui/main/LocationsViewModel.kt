package com.team48.applikasjon.ui.main

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.LocationModel
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.WeatherConverter
import kotlinx.coroutines.launch

class LocationsViewModel(private val repository: Repository) : ViewModel() {

    lateinit var locationModels: MutableList<LocationModel>
    lateinit var selectedLocationModel: LocationModel
    lateinit var map: MapboxMap

    // Utility class to convert weather data into string representation
    var converter = WeatherConverter()

    fun getAllLocations() : LiveData<MutableList<LocationModel>> {
        return repository.getAllLocations()
    }

    fun deleteLocation(position: Int) {
        viewModelScope.launch { repository.deleteLocation(locationModels[position]) }
        locationModels.removeAt(position)
    }


    suspend fun clearDatabase() {
        viewModelScope.launch { repository.clearDatabase() }
        if (this::locationModels.isInitialized) { locationModels.clear() }
    }


    fun addSelected() {
        viewModelScope.launch { repository.addLocation(selectedLocationModel) }
        locationModels.add(selectedLocationModel)
        selectedLocationModel.favourite = true
    }

    fun setMapReference(mapboxMap: MapboxMap) {
        map = mapboxMap
    }


    fun getCameraPositionFromLocation(position: Int): CameraPosition {
        val locationModel: LocationModel = locationModels[position]

        val lat = locationModel.latLong.substringBefore(" ").toDouble()
        val long = locationModel.latLong.substringAfter(" ").toDouble()

        return CameraPosition.Builder()
            .target(LatLng(lat, long, 1.0))
            .zoom(10.0)
            .tilt(0.0)
            .build()
    }

    fun getWeatherFrom(map: MapboxMap, point: LatLng, btb: BottomSheetBehavior<ConstraintLayout>, view: View, location: String) : Boolean {
        // Convert LatLng coordinates to screen pixel and only query the rendered features.
        val pixel = map.projection.toScreenLocation(point)
        val dataArr = arrayOfNulls<Float>(3)

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
            return false
        }

        val l: List<LocationModel> = locationModels.filter { l -> l.name == location }

        if (l.isNotEmpty()) {
            selectedLocationModel = l[0]
            view.findViewById<ImageButton>(R.id.add_favourites).isSelected = true
        } else {
            val latLong = "${point.latitude} ${point.longitude}"
            selectedLocationModel = LocationModel(0,
                location,
                dataArr[0],
                dataArr[1],
                dataArr[2],
                latLong)
            view.findViewById<ImageButton>(R.id.add_favourites).isSelected = false
        }

        view.findViewById<TextView>(R.id.text_location).text = location
        dataArr[0]?.let { view.findViewById<ImageView>(R.id.image_cloud).setImageLevel(it.toInt()) }
        dataArr[1]?.let { view.findViewById<ImageView>(R.id.image_rain).setImageLevel(0.coerceAtLeast(it.toInt())) } // round negative to 0
        dataArr[2]?.let { view.findViewById<ImageView>(R.id.image_temp).setImageLevel(it.toInt()) }

        view.findViewById<TextView>(R.id.text_cloud).text = converter.getCloudDesc(dataArr[0])
        view.findViewById<TextView>(R.id.text_rain).text = converter.getRainDesc(dataArr[1])
        view.findViewById<TextView>(R.id.text_temp).text = converter.getTempDesc(dataArr[2])

        val recString = view.context.getString(R.string.recommendation)
        view.findViewById<TextView>(R.id.text_recommendation).text = String.format(
            recString, converter.getWeatherDesc(dataArr[0], dataArr[1], dataArr[2]))
        btb.state = BottomSheetBehavior.STATE_EXPANDED

        return true
    }
}