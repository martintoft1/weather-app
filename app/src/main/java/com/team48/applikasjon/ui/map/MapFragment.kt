package com.team48.applikasjon.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.main.ViewModelFactory

class MapFragment(val viewModelFactory: ViewModelFactory) : Fragment(R.layout.fragment_map) {

    private lateinit var mapViewModel: MapViewModel
    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mapViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(MapViewModel::class.java)

        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // Initializing map from MapBox servers
        mapView?.getMapAsync{ mapboxMap ->

            // Initializing map style
            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                // Setting camera position over Norway
                //mapboxMap.cameraPosition = mapViewModel.getCamStartPos()

                /*
                // Adding source to style
                style.addSource(VectorSource("metData", tileURL))

                // Creating and adding a layer
                val fillLayer= FillLayer("metFill", "metData")

                // TODO: Create better presentation based on weather type
                fillLayer.setProperties(
                        fillColor(Color.BLUE),
                        fillOpacity(0.4f)
                )
                fillLayer.sourceLayer = tileID

                // Adding layer to style
                style.addLayer(fillLayer)
                 */

            }
        }

        return rootView
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