package com.team48.applikasjon.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.VectorTile

class MapView : Fragment(R.layout.fragment_map_view) {

    var mapView: MapView? = null
    //var camPosition: CameraPosition? = null
    //var camDist: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))

        val view = inflater.inflate(R.layout.fragment_map_view, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.OUTDOORS) {

                val url = "https://test.openmaps.met.no/in2000/map/services"
                val tile = "https://test.openmaps.met.no/services/air_temperature/20210308T060000/001"
                val tiles = arrayOf("https://test.openmaps.met.no/services/air_temperature/20210308T060000/001",
                "https://test.openmaps.met.no/services/air_temperature/20210308T060000/002",
                "https://test.openmaps.met.no/services/air_temperature/20210308T060000/003",
                "https://test.openmaps.met.no/services/air_temperature/20210308T060000/004",
                "https://test.openmaps.met.no/services/air_temperature/20210308T060000/005")


                //val vectorTile = Gson().fromJson(Fuel.get(tile).awaitString(), VectorTile::class.java)

                val tileSet = TileSet(tile, url)

                val vectorSource = VectorSource("vector-source", tileSet)
                it.addSource(vectorSource)
                val circleLayer = CircleLayer("circle-layer-id", "vector-source")
                circleLayer.sourceLayer = tile
                it.addLayer(circleLayer)
            }
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }
}