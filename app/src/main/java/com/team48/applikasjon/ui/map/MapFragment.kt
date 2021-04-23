package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.api.ApiHelper


class MapFragment : Fragment(R.layout.fragment_map) {

    var mapView: MapView? = null
    //var camPosition: CameraPosition? = null
    //var camDist: Float? = null
    var api = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        loadMetdata()


        return view
    }

    private fun loadMetdata() {

        mapView!!.getMapAsync { map ->
            map.setStyle(api.getMapStyle()) { style ->

                val weatherTile = api.getAirTemp()[0]

                val tileSet = TileSet(weatherTile.tilejson, weatherTile.tiles?.get(0))

                val vectorSource = VectorSource("metData", tileSet)
                style.addSource(vectorSource)

                val fillLayer = FillLayer("fillId", "metData")
                fillLayer.setProperties(fillColor(Color.GREEN))
                fillLayer.setProperties(fillOpacity(0.4.toFloat()))
                fillLayer.setSourceLayer(weatherTile.tilestats?.layers?.get(0)?.layer)

                style.addLayer(fillLayer)
            }
        }
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