package com.team48.applikasjon.ui.main

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.DatabaseLocation
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.WeatherConverter
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(private val repository: Repository) : ViewModel() {

    lateinit var databaseLocations: MutableList<DatabaseLocation>
    lateinit var selectedDatabaseLocation: DatabaseLocation
    lateinit var map: MapboxMap
    var selectedPosition: Int = 0


    // Utility class to convert weather data into string representation
    var converter = WeatherConverter()

    fun getAllLocations() : LiveData<MutableList<DatabaseLocation>> {
        return repository.getAllLocations()
    }

    fun deleteLocation(position: Int) {
        viewModelScope.launch { repository.deleteLocation(databaseLocations[position]) }
        databaseLocations.removeAt(position)
    }

    fun deleteSelected() {
        selectedDatabaseLocation.favourite = false
        viewModelScope.launch { repository.deleteLocation(selectedDatabaseLocation) }
        databaseLocations.removeAt(selectedPosition)
    }

    fun addSelected() : Boolean {
        if(!selectedDatabaseLocation.favourite) {
            viewModelScope.launch { repository.addLocation(selectedDatabaseLocation) }
            databaseLocations.add(selectedDatabaseLocation)
            selectedDatabaseLocation.favourite = true
            return true
        }
        return false
    }

    fun moveToPosition(position: Int) {

        // Gjør ingenting om indeksen ikke er gyldig
        if (verifyIndex(position) == -1) {
            return
        }

        val location: DatabaseLocation = databaseLocations[position]
        map.cameraPosition = CameraPosition.Builder()
            .target(LatLng(location.lat, location.long, 1.0))
            .zoom(10.0)
            .tilt(0.0)
            .build()
    }

    // Sjekker om indeks er gyldig
    private fun verifyIndex(position: Int): Int {
        try {
            val location = databaseLocations[position]
        } catch (e: Exception){
            return -1
        }
        return position;
    }

    fun getWeatherFrom(map: MapboxMap, point: LatLng, btb: BottomSheetBehavior<ConstraintLayout>, view: View, location: String) {
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
            Log.d("getWeatherFrom()", "Trykk innenfor Norden!")
            return
        }

        val l = databaseLocations.filter { l -> l.name == location }

        if (l.isNotEmpty()) {
            selectedDatabaseLocation = l[0]
            view.findViewById<ImageButton>(R.id.add_favourites).isSelected = true
        } else {
            selectedDatabaseLocation = DatabaseLocation(0,
                location,
                dataArr[0],
                dataArr[1],
                dataArr[2],
                point.latitude,
                point.longitude)
            view.findViewById<ImageButton>(R.id.add_favourites).isSelected = false
        }

        view.findViewById<TextView>(R.id.text_location).text = location
        dataArr[0]?.let { view.findViewById<ImageView>(R.id.image_cloud).setImageLevel(it.toInt()) }
        dataArr[1]?.let { view.findViewById<ImageView>(R.id.image_rain).setImageLevel(it.toInt()) }
        dataArr[2]?.let { view.findViewById<ImageView>(R.id.image_temp).setImageLevel(it.toInt()) }

        view.findViewById<TextView>(R.id.text_cloud).text = converter.getCloudDesc(dataArr[0])
        view.findViewById<TextView>(R.id.text_rain).text = converter.getRainDesc(dataArr[1])
        view.findViewById<TextView>(R.id.text_temp).text = converter.getTempDesc(dataArr[2])
        btb.state = BottomSheetBehavior.STATE_EXPANDED
    }
}