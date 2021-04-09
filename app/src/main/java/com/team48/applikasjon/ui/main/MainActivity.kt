package com.team48.applikasjon.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.adapter.ViewPagerAdapter
import com.team48.applikasjon.ui.dailyweather.WeatherView
import com.team48.applikasjon.ui.map.MapView
import com.team48.applikasjon.ui.settings.SettingsView

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var weatherFragment: WeatherView
    private lateinit var mapFragment: MapView
    private lateinit var settingsFragment: SettingsView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentContainer = findViewById(R.id.fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setupFragmentContainer()
        setupBottomNavigation()
    }


    private fun setupFragmentContainer() {
        fragmentContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigationView.menu.findItem(R.id.weatherView).isChecked = true
                    1 -> bottomNavigationView.menu.findItem(R.id.mapView).isChecked = true
                    2 -> bottomNavigationView.menu.findItem(R.id.settingsView).isChecked = true
                }
            }
        })

        fragmentContainer.isUserInputEnabled = false // Disable swiping between fragments
        fragmentContainer.post { fragmentContainer.setCurrentItem(1, false) } // Set starting fragment

        weatherFragment = WeatherView()
        mapFragment = MapView()
        settingsFragment = SettingsView()

        val adapter = ViewPagerAdapter.ViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(weatherFragment)
        adapter.addFragment(mapFragment)
        adapter.addFragment(settingsFragment)
        fragmentContainer.adapter = adapter
    }


    private fun setupBottomNavigation() {
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