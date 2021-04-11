package com.team48.applikasjon.ui.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.MetVectorData
import com.team48.applikasjon.data.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        // Forbinder View med ViewModel
        Log.i("MapFragment", "Called ViewModelProvider.get")
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        // Initialisering av mapbox instans
        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))

        // Oppsett av View
        val root = inflater.inflate(R.layout.fragment_map_view, container, false)
        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // ------------------------------------------- //
        // TEMP LØSNING: Legger API-koden her for å teste layers i MapBox
        // ------------------------------------------- //

        val metPath = "https://test.openmaps.met.no/in2000/map/services"
        var metData: List<MetVectorData> = emptyList()

        runBlocking {
            try {
                metData = Gson().fromJson(Fuel.get(metPath).awaitString(), Array<MetVectorData>::class.java).toList()
            } catch (exception: Exception) {
                exception.message?.let { Log.e("getting MET-data", it) }
            }
        }

        val jsonString = Gson().toJson(metData[0])
        Log.i("jsonString metData[0]", jsonString)

        val styleBuild: Style.Builder = Style.Builder().fromJson(jsonString)

        val layers = styleBuild.layers
        Log.i("layers", layers.toString())


        // ------------------------------------------- //
        // ------------------------------------------- //

        mapView?.getMapAsync(OnMapReadyCallback { mapboxMap ->

            mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->

                // Ved bruk av lambdaen(?) (style ->) så kan vi legge til data til MAXBOX_STREETS style

                // Dette kan gjøres med:
                //style.addLayer(layer)

                // Men inntil videre er variabelen layers på linje 76 tom, ukjent hvorfor

            }
        })

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