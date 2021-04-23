package com.team48.applikasjon.ui.dailyweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.MapViewModel

class WeatherFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        weatherViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(WeatherViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }
}
