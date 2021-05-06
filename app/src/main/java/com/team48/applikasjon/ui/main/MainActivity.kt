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
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.dailyweather.WeatherFragment
import com.team48.applikasjon.ui.main.adapters.FragmentContainerAdapter
import com.team48.applikasjon.ui.map.MapFragment
import com.team48.applikasjon.ui.settings.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 44
    private var result : String = ""

    private val repository = Repository()
    private val viewModelFactory = ViewModelFactory(repository)

    private val weatherFragment  = WeatherFragment(viewModelFactory)
    private val mapFragment      = MapFragment(viewModelFactory)
    private val settingsFragment = SettingsFragment(viewModelFactory)


    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentContainer = findViewById(R.id.fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setupFragmentContainer()
        setupBottomNavigation()

        // Location Manager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastPosition()

    }

    @SuppressLint("MissingPermission")
    fun getLastPosition(): Location? {

        var locationResult: Location? = null

        if (checkPermission()) {
           if (isLocationEnabled()) {

               fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        result = "" + location.latitude + ":" + location.longitude
                        Log.d("location.latitude", location.latitude.toString())
                        Log.d("location.longitude", location.longitude.toString())
                        Log.d("LOCATION 1:", result)

                        locationResult = location
                    }

               }

           } else { // isLocationEnabled == false
               Log.d("Location not enabled in settings", "enable it")
               /*
               Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
               val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
               startActivity(intent)
                */
           }

        } else { // checkPermission == false
            requestPermission()
        }

        return locationResult
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        // Initializing LocationRequest
        // object with appropriate methods
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, myLooper())
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            result = "" + lastLocation.longitude + ":" + lastLocation.latitude
            Log.d("LOCATION 2:", result)
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
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun setupFragmentContainer() {
        val adapter = FragmentContainerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(weatherFragment)
        adapter.addFragment(mapFragment)
        adapter.addFragment(settingsFragment)
        fragmentContainer.adapter = adapter

        fragmentContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    // Valgt ikon i bottom navigation blir "checked"
                    0 -> bottomNavigationView.menu.findItem(R.id.weatherView).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.mapView).isChecked = true
                    2 -> bottomNavigationView.menu.findItem(R.id.settingsView).isChecked = true
                }
            }
        })

        // Skru av swipe-animasjon mellom fragments
        fragmentContainer.isUserInputEnabled = false
        // Setter fragment som åpnes først
        fragmentContainer.post { fragmentContainer.setCurrentItem(1, false) }
    }

    private fun setupBottomNavigation() {
        // Bytter fragment ved bottomnav navigering
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.weatherView -> fragmentContainer.setCurrentItem(0, false)
                R.id.mapView -> fragmentContainer.setCurrentItem(1, false)
                R.id.settingsView -> fragmentContainer.setCurrentItem(2, false)
            }
            false
        }
    }
}