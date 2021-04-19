package com.team48.applikasjon.ui.main

import com.team48.applikasjon.ui.main.adapter.MainAdapter
import com.team48.applikasjon.ui.dailyweather.WeatherViewModel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.mapboxsdk.Mapbox
import com.team48.applikasjon.R

import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiService
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.StatusUI

class MainActivity : AppCompatActivity() {

    private lateinit var WeatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing MapBox instance
        //Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        val apiServiceImpl = ApiServiceImpl()
        val apiHelper = ApiHelper(apiServiceImpl)
        val repository = Repository(apiHelper)
        val viewModelFactory = ViewModelFactory(repository)



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.weatherView, R.id.mapView, R.id.settingsView))
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)

    }

}