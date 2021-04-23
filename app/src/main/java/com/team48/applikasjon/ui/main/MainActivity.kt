package com.team48.applikasjon.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team48.applikasjon.R
import androidx.viewpager2.widget.ViewPager2
import com.mapbox.mapboxsdk.Mapbox
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.dailyweather.WeatherFragment
import com.team48.applikasjon.ui.main.adapter.FragmentContainerAdapter
import com.team48.applikasjon.ui.map.MapFragment
import com.team48.applikasjon.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

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
                R.id.weatherView  -> fragmentContainer.setCurrentItem(0, false)
                R.id.mapView      -> fragmentContainer.setCurrentItem(1, false)
                R.id.settingsView -> fragmentContainer.setCurrentItem(2, false)
            }
            false
        }
    }
}