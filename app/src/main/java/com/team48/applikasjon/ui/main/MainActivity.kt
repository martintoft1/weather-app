package com.team48.applikasjon.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper.myLooper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.favourites.LocationsFragment
import com.team48.applikasjon.ui.main.adapters.FragmentContainerAdapter
import com.team48.applikasjon.ui.map.MapFragment
import com.team48.applikasjon.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private val PERMISSION_ID = 44

    private val locationsFragment   = LocationsFragment()
    private val mapFragment         = MapFragment()
    private val settingsFragment    = SettingsFragment()

    // Felles repository for alle ViewModels, via ViewModelFactory
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Lifecycle", "MainActivity onCreate")

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = Repository(this)
        viewModelFactory = ViewModelFactory(repository)

        fragmentContainer = findViewById(R.id.fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Oppsett av navigation bar med fragmenter
        setupFragmentContainer()
        setupBottomNavigation()


        // Location Manager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

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

    @SuppressLint("MissingPermission")
    fun updateLocation()  {

        if (checkPermission()) {
           if (isLocationEnabled()) {

               fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        // Leverer brukerposisjon til MapFragment
                        mapFragment.updateUserLocation(location)
                    }
               }

           } else { // isLocationEnabled == false
               // Do nothing
               Log.d("isLocationEnabled()", "== false")
               // TODO: Denne kan fjernes når vi har testet mer, usikker når alternativet forekommer
           }

        } else { // checkPermission == false
            requestPermission()
        }
    }

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

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            mapFragment.updateUserLocation(lastLocation)
        }
    }

    private fun checkPermission(): Boolean {
        return  ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

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

    fun getDarkModeActivatedStatus(): Boolean {
        return settingsFragment.getDarkModeButtonStatus()
    }

    private fun setupFragmentContainer() {
        val adapter = FragmentContainerAdapter(supportFragmentManager, lifecycle)
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

        // Skru av swipe-animasjon mellom fragments
        fragmentContainer.isUserInputEnabled = false

        // Initialiserer fragmentene som appen ikke starter i
        fragmentContainer.setCurrentItem(0, false)
        fragmentContainer.setCurrentItem(1, false)
        fragmentContainer.setCurrentItem(2, false)

        // Setter fragment som åpnes først
        fragmentContainer.post { fragmentContainer.setCurrentItem(1, false) }
    }

    private fun setupBottomNavigation() {
        // Bytter fragment ved bottomnav navigering
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.locationsView -> {
                    fragmentContainer.setCurrentItem(0, true)
                }
                R.id.mapView -> fragmentContainer.setCurrentItem(1, true)
                R.id.settingsView -> fragmentContainer.setCurrentItem(2, true)
            }
            false
        }
    }

    fun moveCamera(cameraPosition: CameraPosition) {
        mapFragment.setLocation(cameraPosition)
        fragmentContainer.post { fragmentContainer.setCurrentItem(1, true) }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "MainActivity onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "MainActivity onStart")
    }

    override fun onPause() {
        super.onPause()
        //mapFragment.onPause()
        //settingsFragment.onPause()
        //locationsFragment.onPause()
        Log.d("Lifecycle", "MainActivity onPause")
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
        settingsFragment.onResume()
        locationsFragment.onResume()
        Log.d("Lifecycle", "MainActivity onResume")
    }
}