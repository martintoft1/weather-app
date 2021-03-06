package com.team48.applikasjon.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper.myLooper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.LocationModel
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.locations.LocationsFragment
import com.team48.applikasjon.ui.main.adapters.FragmentContainerAdapter
import com.team48.applikasjon.ui.map.MapFragment
import com.team48.applikasjon.ui.settings.SettingsFragment

/* En MainActivity for hele applikasjonen
 * Appen baserer seg på Fragmenter for visning til bruker */
class MainActivity : AppCompatActivity() {

    // Fragmentrelaterte variabler
    private lateinit var fragmentContainer: ViewPager2
    private lateinit var adapter: FragmentContainerAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    // Lokasjonsrelaterte variabler
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private val PERMISSION_ID = 44

    // Initialisering av fragmentene
    private val locationsFragment   = LocationsFragment()
    private val mapFragment         = MapFragment()
    private val settingsFragment    = SettingsFragment()

    // Felles repository for alle ViewModels, via ViewModelFactory
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sjekker om nettverk er tilgjengelig
        if (!isNetworkAvailable()) {

            // Hvis ikke tilgjengelig, print feilmelding og unngå at Mapbox kjøres opp
            Toast.makeText(
                this,
                "Nettverk er utilgjengelig! Restart appen med nettverk.",
                Toast.LENGTH_LONG).show()
            return
        }

        // Validerer Mapbox-trafikken
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        // Initialiserer et felles repository og ViewModelFactory for hele appen
        repository = Repository(this)
        viewModelFactory = ViewModelFactory(repository)

        // Beholder for fragmenter og opprettelse av navigeringsmenyen i appen
        fragmentContainer = findViewById(R.id.fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Oppsett av navigasjonsmenyen med fragmenter
        setupFragmentContainer()
        setupBottomNavigation()

        // Standard Google Play-lokasjonstjeneste
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    // Sjekker tilstand til nettverksforbindelsen
    private fun isNetworkAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetwork
        var isAvailable = false
        if (networkInfo != null)
            isAvailable = true

        return isAvailable
    }

    // Grenesnitt for ViewModels
    fun getViewModelFactory(): ViewModelFactory {
        return viewModelFactory
    }

    // Støttefunksjon for kommunikasjon mellom Settings- og MapFragment
    fun changeMapStyle(styleResource: Int, visualMode: Int) {
        mapFragment.changeStyle(styleResource, visualMode)
    }

    // Grensesnitt mellom MapFragment og SettingsFragment, relatert til location-knapp
    fun getLocationButtonStatus(): Boolean {
        return settingsFragment.getLocationButtonStatus()
    }

    // Grensesnitt for å fjerne favoritt
    fun unfavouriteCurrent() {
        mapFragment.unfavouriteCurrent()
    }

    // Standard funksjons for å håndtere innhenting av brukerlokasjon
    @SuppressLint("MissingPermission")
    fun updateLocation()  {

        // Sjekker om tillatelse er gitt
        if (checkPermission()) {

            // Sjekker om brukerlokasjon er aktivert
            @Suppress("ControlFlowWithEmptyBody")
            if (isLocationEnabled()) {

                // Innhenter brukerlokasjon om den ikke eksiterer
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        // Leverer brukerposisjon til MapFragment
                        mapFragment.updateUserLocation(location)
                    }
               }

           } else { // isLocationEnabled == false
               // Appen skal ikke gjentagende ganger be bruker om å aktivere lokasjon
           }

        } else { // checkPermission == false

            // Innhenter tillatelse
            requestPermission()
        }
    }

    // Standardfunksjon for å innhente brukerlokasjon
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, myLooper())
    }

    // Callback relatert til innhenting av brukerlokasjon
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            mapFragment.updateUserLocation(lastLocation)
        }
    }

    // Sjekker om det er gitt tillatelse for innhenting av brukerlokasjon
    private fun checkPermission(): Boolean {
        return  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Innhenter tilattelse
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

    // Sjekker om brukerlokasjon er aktivert i innstillinger
    private fun isLocationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && settingsFragment.getLocationButtonStatus()
    }

    // Kalles på via switch i settings
    fun enableLocation() {
        updateLocation()
    }

    // Kalles på via switch i settings
    fun disableLocation() {
        val nullLocation: Location? = null
        mapFragment.updateUserLocation(nullLocation)
    }

    // For innhenting av darkmode-status
    fun getDarkModeActivatedStatus(): Boolean {
        return settingsFragment.getDarkModeButtonStatus()
    }

    // Toggler favorittknappen
    fun updateNoFavourites(status: Int) {
        locationsFragment.view?.findViewById<TextView>(R.id.tv_no_favourites)?.visibility = status
    }

    // Oppsett av fragmentcontainer
    private fun setupFragmentContainer() {
        adapter = FragmentContainerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(locationsFragment)
        adapter.addFragment(mapFragment)
        adapter.addFragment(settingsFragment)
        fragmentContainer.adapter = adapter

        fragmentContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    // Valgt ikon i bottom navigation blir "checked"
                    0 -> bottomNavigationView.menu.findItem(R.id.locationsView).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.mapView).isChecked = true
                    2 -> bottomNavigationView.menu.findItem(R.id.settingsView).isChecked = true
                }
            }
        })

        // Skru av swipe-funksjon mellom fragments
        fragmentContainer.isUserInputEnabled = false

        // Initialiserer fragmentene som appen ikke starter i
        fragmentContainer.setCurrentItem(0, false)
        fragmentContainer.setCurrentItem(1, false)
        fragmentContainer.setCurrentItem(2, false)

        // Setter fragment som åpnes først
       fragmentContainer.post { fragmentContainer.setCurrentItem(1, false) }
    }

    // Oppsett av navigasjonsmeny i bunn av appen
    private fun setupBottomNavigation() {
        // Bytter fragment ved bottomnav navigering
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.locationsView -> fragmentContainer.setCurrentItem(0, true)
                R.id.mapView -> fragmentContainer.setCurrentItem(1, true)
                R.id.settingsView -> fragmentContainer.setCurrentItem(2, true)
            }
            false
        }
    }

    // Grensesnitt for flytting av kamera
    fun moveCamera(cameraPosition: CameraPosition, locationModel: LocationModel) {
        mapFragment.setLocation(cameraPosition, locationModel)
        fragmentContainer.post { fragmentContainer.setCurrentItem(1, true) }
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
        settingsFragment.onResume()
        locationsFragment.onResume()
    }
}