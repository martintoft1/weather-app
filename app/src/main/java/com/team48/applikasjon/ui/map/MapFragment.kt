package com.team48.applikasjon.ui.map

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.adapters.SpinnerAdapter


class MapFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var rootView: View
    private lateinit var repository: Repository
    private lateinit var mapViewModel: MapViewModel
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var spinner: Spinner
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

    private fun getPosition(): CameraPosition {

        val cameraPosition: CameraPosition

        // Henter posisjon via kall i MainActivity
        val location: Location? = (activity as MainActivity).getLastPosition()

        if (location != null) {

            Log.d("Location:", "Location is acquired!")
            cameraPosition = CameraPosition.Builder()
               .target(LatLng(location.latitude, location.longitude, 1.0))
               .zoom(3.0)
               .tilt(0.0)
               .build()
        } else {

            Log.d("Location:", "Location is not acquired")
            cameraPosition = CameraPosition.Builder()
                .target(LatLng(62.0, 16.0, 1.0))
                .zoom(3.0)
                .tilt(0.0)
                .build()
        }
        return cameraPosition

    }

    private fun setupMap(savedInstanceState: Bundle?) {

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // Initializing map from MapBox servers
        mapView?.getMapAsync{ map ->

            // Setting camera position over Norway
            map.cameraPosition = getPosition()

            // Initializing map style
            val customStyle = Style.Builder().fromUri(getString(R.string.mapStyleUri))
            map.setStyle(customStyle) { style ->

                // Oppretter layers når data er tilgjengelig etter API-kall
                mapViewModel.updateWeather(style)


                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        // TODO: Fikse håndtering av spinner startpos = 0 (cloud)

                        /* Spinner position index: noLayer = 0, cloud = 1, umbrella/precipitiation = 2, temp = 3 */
                        // Dataready = true når API-kall er ferdig
                        if (mapViewModel.dataReady) {

                            // Position = No filter
                            if (position == 0) mapViewModel.hideAllLayers()

                            // Position = 1: Clouds | 2: Precipitiation | 3: airTemp
                            else mapViewModel.chooseLayer(style, position - 1)

                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }

            map.addOnMapClickListener { point ->
                mapViewModel.getWeatherFrom(map, point)
                true
            }
        }
    }

    private fun setupSpinner() {
        spinner = rootView.findViewById(R.id.spinner_weather_filter)
        val icons = mutableListOf<Int>()
        icons.add(R.drawable.ic_empty_filter)
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