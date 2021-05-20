package com.team48.applikasjon.ui.map

import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.LocationModel
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.SharedViewModel
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.adapters.SpinnerAdapter
import com.team48.applikasjon.utils.Animator
import com.team48.applikasjon.utils.WeatherConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mapViewModel: MapViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var spinner: Spinner
    private lateinit var locationButton: ImageView
    private lateinit var mapboxMap: MapboxMap
    private var symbolManager: SymbolManager? = null
    private var userLocation: Location? = null
    var mapView: MapView? = null

    private val converter = WeatherConverter()
    private val ID_ICON = "ic_marker"
    private var symbol: Symbol? = null
    private val animator = Animator()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("onCreateView", "MapFragment")
        rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModelFactory()
        setupViewModel()
        setupMap(savedInstanceState)
        setupSpinner()
        setupBottomSheet()
        setupLocationButton()
    }

    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    // Oppsett av ViewModel
    private fun setupViewModel() {
        mapViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(MapViewModel::class.java)

        sharedViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(SharedViewModel::class.java)

        sharedViewModel.getAllLocations().observe(viewLifecycleOwner, {
            sharedViewModel.locationModels = it
        })
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

    fun setLocation(cameraPosition: CameraPosition, locationModel: LocationModel) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        mapboxMap.cameraPosition = cameraPosition
        val lat: Double = locationModel.latLong.split(" ")[0].toDouble()
        val lon: Double = locationModel.latLong.split(" ")[1].toDouble()
        val p = LatLng(lat, lon)
        addMarker(p)
        updateBottomSheet(locationModel)
    }

    // Kalles på av MainActivity, oppdaterer lokal variabel userLocation
    fun updateUserLocation(location: Location?) {
        userLocation = location
    }

    // Endrer stil ved valg i innstillinger
    fun changeStyle(styleResource: Int, visualMode: Int) {
        mapboxMap.setStyle(Style.Builder().fromUri(getString(styleResource))) { style ->
            // Layers må legges til på nytt.
            createMarker(style)
            // visualMode = 0: Default/Light, = 1: Dark
            mapViewModel.addAllLayers(style, 1)
        }
    }

    fun createMarker(style: Style) {
        // Initialiserer SymbolManager til Marker
        if (mapView != null) {
            symbolManager = SymbolManager(mapView!!, mapboxMap, style)
            symbolManager?.iconAllowOverlap = true
            symbolManager?.textAllowOverlap = true
        }

        // Legger marker drawable ressurs til style
        val drawable: Drawable? = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_marker,
            null
        )
        if (drawable != null) { style.addImage(ID_ICON, drawable) }
    }

    // Kan bli tvinget gjennom av repo, ved endringer i settings
    fun setupMap(savedInstanceState: Bundle?) {

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        // Initialiserer Mapbox-kartet fra Mapbox-server
        mapView?.getMapAsync { map ->

            // Lagre peker til map for Fragment og ViewModel
            mapboxMap = map

            sharedViewModel.setMapReference(map)

            // Setter kameraposisjon til over Norge initielt
            map.cameraPosition = mapViewModel.getCamNorwayPos()

            // Henter status på darkmode-knapp
            val status = (activity as MainActivity).getDarkModeActivatedStatus()
            val styleResource = if (status) mapViewModel.getDarkModeStyleResource()
                                else        mapViewModel.getDefaultStyleResource()

            // Mapbox setter stil via ressurs på Mapbox-server
            map.setStyle(getString(styleResource)) { style ->

                // Oppretter layers når data er tilgjengelig etter API-kall
                mapViewModel.updateWeather(style)

                createMarker(style)
            }

            map.addOnMapLongClickListener { point ->
                addMarker(point)
                getLocationFrom(map, point)
                true
            }


            map.addOnMapClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state =  BottomSheetBehavior.STATE_COLLAPSED
                true
            }
        }
    }


    private fun addMarker(point: LatLng) : Boolean {
        if (symbolManager == null) { return false }

        // Oppdater eksisterende markør
        if (symbol != null) {
            symbol!!.latLng = point
            symbolManager?.update(symbol)
            Log.d("addMarker", "Updating marker")
        }

        // Opprett ny markør
        else {
            Log.d("addMarker", "Creating marker")
            symbol = symbolManager?.create(
                SymbolOptions()
                    .withLatLng(point)
                    .withIconImage(ID_ICON)
                    .withIconAnchor(ICON_ANCHOR_BOTTOM)
            )
        }
        return true
    }

    // Oppsett av spinner og håndtering av valgene
    private fun setupSpinner() {
        spinner = rootView.findViewById(R.id.spinner_weather_filter)
        val icons = mutableListOf<Int>()
        icons.add(R.drawable.c_cloud_medium)
        icons.add(R.drawable.c_rain_medium)
        icons.add(R.drawable.c_sun)
        icons.add(R.drawable.c_x)

        spinnerAdapter  = SpinnerAdapter(requireContext(), icons)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(icons.size - 1)

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
                    if (position == 3) mapViewModel.hideAllLayers()

                    // Position = 1: Clouds | 2: Precipitiation | 3: airTemp
                    else mapViewModel.chooseLayer(position)

                }
            }

            // Do nothing
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    // Oppsett av bottomsheet og håndtering av valg
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Favorittknapp onClick
        val button_fav: ImageButton = rootView.findViewById(R.id.add_favourites)
        button_fav.setOnClickListener {

            // Legg til i favoritter
            if (!button_fav.isSelected) {
                // Fjern "ingen favoritter" tekst hvis synlig
                updateNoFavourites(View.GONE)
                sharedViewModel.addSelected()
                animator.expandHeart(button_fav)
                button_fav.isSelected = true

                Toast.makeText(requireContext(), "Lagret i favoritter!", LENGTH_SHORT).show()
            }
        }
    }


    private fun updateBottomSheet(l: LocationModel) {
        rootView.findViewById<TextView>(R.id.text_location).text = l.name
        l.cloud_percentage?.let { rootView.findViewById<ImageView>(R.id.image_cloud).setImageLevel(it.toInt()) }
        l.rain_mm?.let { rootView.findViewById<ImageView>(R.id.image_rain).setImageLevel(it.toInt()) }
        l.temp_celsius?.let { rootView.findViewById<ImageView>(R.id.image_temp).setImageLevel(it.toInt()) }

        rootView.findViewById<TextView>(R.id.text_cloud).text = converter.getCloudDesc(l.cloud_percentage)
        rootView.findViewById<TextView>(R.id.text_rain).text = converter.getRainDesc(l.rain_mm)
        rootView.findViewById<TextView>(R.id.text_temp).text = converter.getTempDesc(l.temp_celsius)
        rootView.findViewById<TextView>(R.id.text_recommendation).text = converter.getWeatherDesc(l.cloud_percentage, l.rain_mm, l.temp_celsius)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        rootView.findViewById<ImageButton>(R.id.add_favourites).isSelected = true
    }

    fun unfavouriteCurrent() {
        rootView.findViewById<ImageButton>(R.id.add_favourites).isSelected = false
        Log.d("unfavourite", sharedViewModel.locationModels.size.toString())
        /* Don't show bottom sheet if no location is selected */
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    fun updateNoFavourites(status: Int) {
        (activity as MainActivity).updateNoFavourites(status)
    }

    private fun getLocationFrom(map: MapboxMap, point: LatLng) {
        val reverseGeocode = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapbox_access_token))
            .query(Point.fromLngLat(point.longitude, point.latitude))
            .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
            .build()

        lateinit var placeName: String
        reverseGeocode.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {
                val results = response.body()!!.features()
                if (results.size > 0) {
                    placeName = results[0].placeName()!!.toString().substringBeforeLast(",")

                    sharedViewModel.getWeatherFrom(
                        map,
                        point,
                        bottomSheetBehavior,
                        rootView,
                        placeName
                    )
                } else {
                    Toast.makeText(requireContext(), "Ingen lokasjon funnet", LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })
    }

    // Oppsett av locationknapp og logikk for håndtering av valg
    private fun setupLocationButton() {
        locationButton = rootView.findViewById(R.id.locationPicker)

        // Listener for location-knapp
        locationButton.setOnClickListener {

            // Sjekker om brukerlokasjon er tillatt i settings, true hvis tillatt
            if ((activity as MainActivity).getLocationButtonStatus()) {
                (activity as MainActivity).updateLocation()

                // Sjekker om lokasjonen er gyldig
                if (setUserLocation() != null)
                    mapboxMap.cameraPosition = setUserLocation()!!
                else Toast.makeText(
                    requireContext(),
                    "Brukerlokasjon ikke tilgjengelig",
                    LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Brukerlokasjon må tillates i innstillinger",
                    LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        Log.d("Lifecycle", "MapFragment onStart")
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        Log.d("Lifecycle", "MapFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        Log.d("Lifecycle", "MapFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        Log.d("Lifecycle", "MapFragment onStop")
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