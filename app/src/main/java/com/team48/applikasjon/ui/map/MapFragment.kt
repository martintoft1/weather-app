package com.team48.applikasjon.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.adapters.SpinnerAdapter

class MapFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var rootView: View
    private lateinit var mapViewModel: MapViewModel
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var repository: Repository
    private lateinit var spinner: Spinner
    private lateinit var layerURL: String
    private var lastSpinnerPosition: Int = -1

    // Indeks basert på spinnerindekser, 0 indikerer at layer ikke er initialisert
    private var layerLoaded: MutableList<Int> = mutableListOf(0,0,0)

    var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupMap(savedInstanceState)
        setupSpinner()
    }

    private fun setupViewModel() {
        mapViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(MapViewModel::class.java)

        repository = mapViewModel.repository
    }

    private fun setupMap(savedInstanceState: Bundle?) {

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // Initializing map from MapBox servers
        mapView?.getMapAsync{ map ->

            // Setting camera position over Norway
            map.cameraPosition = mapViewModel.getCamStartPos()

            // Initializing map style
            val customStyle = Style.Builder().fromUri(getString(R.string.mapStyleUri))
            map.setStyle(customStyle) { style ->

                // Henter værdata fra oppdatert liste i mapViewModel
                mapViewModel.updateWeather()

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        // TODO: Fikse håndtering av spinner startpos = 0 (cloud)

                        /* Spinner position index: cloud = 0, umbrella/precipitiation = 1, temp = 2 */
                        // Dataready = true når API-kall er ferdig
                        if (mapViewModel.dataReady) {

                            // Initialiser layer om det ikke er gjort tidligere
                            if (layerLoaded[position] == 0) {
                                addNewLayer(style, position)
                                layerLoaded[position] = 1
                            }

                            // Endre synlighet av layers
                            else if (position != lastSpinnerPosition) {
                                changeLayer(style, lastSpinnerPosition, position)
                            }

                            // Oppdaterer spinnerhistorikk
                            lastSpinnerPosition = position
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // TODO: Legge til mulighet for å fjerne filter
                    }
                }
            }
        }
    }

    private fun updateLayerURL(position: Int) {
        layerURL = mapViewModel.getWeatherTypeURL(position)
    }

    private fun changeLayer(style: Style, oldPosition: Int, newPosition: Int) {

        var oldLayer: Layer? = null
        var newLayer: Layer? = null

        // Henter layers-referanse for forrige og neste layer
        for (layer in style.layers) {
            if (layer.id == "layer$oldPosition") oldLayer = layer
            else if (layer.id == "layer$newPosition") newLayer = layer

            if (oldLayer != null && newLayer != null) break
        }

        // Endre synlighet av layers
        oldLayer!!.setProperties(visibility(NONE))
        newLayer!!.setProperties(visibility(VISIBLE))

    }

    private fun addNewLayer(style: Style, position: Int) {

        val sourceString = "source$position"
        val layerString = "layer$position"

        // Oppdaterer layerURL fra værtype med indeks: position
        updateLayerURL(position)

        // Adding source to style
        val vectorSource = VectorSource(sourceString, layerURL)
        style.addSource(vectorSource)

        // Creating layer
        val fillLayer = FillLayer(layerString, sourceString)

        // Setting layer properties
        mapViewModel.setLayerProperties(fillLayer, position)

        // Adding sourcelayer ID
        fillLayer.sourceLayer = mapViewModel.getIDfromURL(layerURL)

        // Adding layer to style
        style.addLayer(fillLayer)

    }

    private fun setupSpinner() {
        spinner = rootView.findViewById(R.id.spinner_weather_filter)
        val icons = mutableListOf<Int>()
        icons.add(R.drawable.ic_cloud)
        icons.add(R.drawable.ic_umbrella)
        icons.add(R.drawable.ic_temperature)

        spinnerAdapter  = SpinnerAdapter(requireContext(), icons)
        spinner.adapter = spinnerAdapter
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