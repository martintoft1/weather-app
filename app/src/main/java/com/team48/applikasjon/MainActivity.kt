package com.team48.applikasjon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.reflect.Type
import java.net.URI

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)
        gson = Gson()
        loadMapbox(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                loadMetdata()
            } catch (exception: Exception) {
                Log.d("loadMetdata failed", exception.toString())
            }
        }
    }

    @Throws(Exception::class)
    private suspend fun loadMetdata() {
        val url = "https://test.openmaps.met.no/in2000/map/services"
        val eks = "https://test.openmaps.met.no/services/air_temperature/20210308T060000/001"

        //val founderListType: Type = object : TypeToken<MutableList<VectorTile?>?>() {}.type
        val vectorTile = gson.fromJson(Fuel.get(eks).awaitString(), VectorTile::class.java)

        Log.d("vectortiles", vectorTile.toString())


        mapView.getMapAsync { map ->
            map.getStyle {
                val geoJsonUrl = URI(eks)
                    val geoJsonSource = GeoJsonSource("geojson-source", geoJsonUrl)
                    it.addSource(geoJsonSource)
            }
        }
    }


    private fun loadMapbox(savedInstanceState: Bundle?) {
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            map.setStyle(Style.OUTDOORS)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }


 /*
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
  */
    /*
    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }
    */
}