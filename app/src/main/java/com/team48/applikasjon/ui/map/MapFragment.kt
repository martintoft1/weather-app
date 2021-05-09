package com.team48.applikasjon.ui.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
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
    private lateinit var locationButton: ImageView
    private lateinit var mapboxMap: MapboxMap
    private var userLocation: Location? = null
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
        setupLocationButton()
    }

    // Oppsett av ViewModel
    private fun setupViewModel() {
        mapViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(MapViewModel::class.java)

        repository = mapViewModel.repository
    }

    // Settes brukerlokasjon i kart basert på variabelen userLocation
    private fun setUserLocation(): CameraPosition? {

        if (userLocation == null) return null
        return CameraPosition.Builder()
                .target(LatLng(userLocation!!.latitude, userLocation!!.longitude, 1.0))
                .zoom(10.0)
                .tilt(0.0)
                .build()
    }

    // Kalles på av MainActivity, oppdaterer lokal variabel userLocation
    fun updateUserLocation(location: Location?) {
        userLocation = location
    }

    // Endrer stil ved valg i innstillinger
    fun changeStyle(styleResource: Int, visualMode: Int) {
        mapboxMap.setStyle(Style.Builder().fromUri(getString(styleResource))) { style ->

            // Layers må legges til på nytt.
            // visualMode = 0: Default/Light, = 1: Dark
            mapViewModel.addAllLayers(style, 1)
        }
    }

    // Kan bli tvinget gjennom av repo, ved endringer i settings
    fun setupMap(savedInstanceState: Bundle?) {

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // Initialiserer Mapbox-kartet fra Mapbox-server
        mapView?.getMapAsync { map ->

            // Lagre peker til map for Fragment og ViewModel
            mapboxMap = map
            mapViewModel.map = map

            // Setter kameraposisjon til over Norge initielt
            map.cameraPosition = mapViewModel.getCamNorwayPos()

            // Mapbox setter stil via ressurs på Mapbox-server
            map.setStyle(getString(mapViewModel.getDefaultStyleResource())) { style ->

                // Oppretter layers når data er tilgjengelig etter API-kall
                mapViewModel.updateWeather(style)

                // Listener for filtervalg i spinner
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    // Håndtering av valg i spinner
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {

                        /* Spinner position index: noLayer = 0, cloud = 1, umbrella/precipitiation = 2, temp = 3 */
                        // Dataready = true når API-kall er ferdig
                        if (mapViewModel.dataReady) {

                            // Position = No filter
                            if (position == 0) mapViewModel.hideAllLayers()

                            // Position = 1: Clouds | 2: Precipitiation | 3: airTemp
                            else mapViewModel.chooseLayer(position - 1)

                        }
                    }

                    // Do nothing
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }

            // Listener for trykking i kart
            map.addOnMapClickListener { point ->
                mapViewModel.getWeatherFrom(map, point)
                true
            }

            // Listener for location-knapp
            locationButton.setOnClickListener {

                // Sjekker om brukerlokasjon er tillatt i settings, true hvis tillatt
                if ((activity as MainActivity).getLocationButtonStatus()) {
                    (activity as MainActivity).updateLocation()

                    // Sjekker om lokasjonen er gyldig
                    if (setUserLocation() != null)
                        mapboxMap.cameraPosition = setUserLocation()!!
                    else Toast.makeText(requireContext(),
                            "Brukerlokasjon ikke tilgjengelig",
                            Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireContext(),
                            "Brukerlokasjon må tillates i innstillinger",
                            Toast.LENGTH_LONG).show()
                }
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

    private fun setupLocationButton() {
        locationButton = rootView.findViewById(R.id.locationPicker)
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