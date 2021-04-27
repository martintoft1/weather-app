package com.team48.applikasjon.ui.map

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.adapters.SpinnerAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapFragment(val viewModelFactory: ViewModelFactory) : Fragment(), AdapterView.OnItemSelectedListener  {
    private lateinit var rootView: View
    private lateinit var mapViewModel: MapViewModel
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var weatherTile: VectorTile
    private lateinit var repository: Repository

    var mapView: MapView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

                // her må vi vente til apiet er lastet inn
                // TODO: IMPLEMENTERE LIVEDATA FOR REPOSITORY ELLER APISERVICEIMPL
                // runblocking på 5 sekunder er midlertidig løsning

                // jEg eR en dYkTIg ANdrOiDutVikLer ?:)
                runBlocking {
                    delay(9000)
                }
                val weatherType: Int = 0

                when (weatherType) {
                    0 -> weatherTile = repository.getAirTemp()[0]
                    1 -> weatherTile = repository.getClouds()[0]
                    2 -> weatherTile = repository.getPrecipitation()[0]
                    3 -> weatherTile = repository.getPressure()[0]
                }


                var tileSet: TileSet

                mapViewModel.tileSet.observe(viewLifecycleOwner, Observer {

                    // TileSet based on update value in ViewModel
                    tileSet = it

                    // Adding source to style
                    val vectorSource = VectorSource("weatherData", tileSet)
                    style.addSource(vectorSource)

                    // Creating and adding a layer
                    val fillLayer = FillLayer("airTemp", "weatherData")
                    mapViewModel.setLayerProperties(fillLayer, "airTemp")
                    fillLayer.sourceLayer = weatherTile.getTileId()

                    // Adding layer to style
                    style.addLayer(fillLayer)

                })
            }
        }
    }


    private fun setupSpinner() {
        val spinner: Spinner = rootView.findViewById(R.id.spinner_weather_filter)
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

    /* Spinner onItemSelected */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Her skal filter endres basert på valgt element i spinner")
    }

    /* Spinner onNothingSelected */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}