package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.MetVectorData
import kotlinx.coroutines.runBlocking

class MapView : Fragment() {

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

        // ----------------------------------------------------------- //
        // TODO: Most of the code here will be moved to Api/Repo/MapModel
        // ----------------------------------------------------------- //

        // Binding fragment with ViewModel
        Log.i("MapFragment", "Called ViewModelProvider.get")
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        // Initializing MapBox instance
        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))

        // Creating view
        val root = inflater.inflate(R.layout.fragment_map_view, container, false)
        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        val metPath = "https://test.openmaps.met.no/in2000/map/services"
        var metData: List<MetVectorData> = emptyList()

        // TODO: Remove runBlocking when moving code to ApiService
        runBlocking {
            try {
                metData = Gson().fromJson(Fuel.get(metPath).awaitString(), Array<MetVectorData>::class.java).toList()
            } catch (exception: Exception) {
                exception.message?.let { Log.e("getting MET-data", it) }
            }
        }

        // Access metadata-URL for arbitrary dataset, example is air temp
        // TODO: Implement logic for choosing which kind of weather stats to be shown
        val tileURL: String = metData[0].url.toString()

        // Parse URL to get layer-ID
        val tileID: String = tileURL.substringAfterLast("/")
        Log.d("tileID", tileID)

        // Initializing map from MapBox servers
        mapView?.getMapAsync{ mapboxMap ->

            // Initializing map style
            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                // Setting camera position over Norway
                mapboxMap.cameraPosition = mapViewModel.getCamStartPos()

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

            }
        }

        return root
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