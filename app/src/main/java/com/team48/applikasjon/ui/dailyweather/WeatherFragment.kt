package com.team48.applikasjon.ui.dailyweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.MapViewModel

class WeatherFragment() : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModelFactory()
    }

    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    override fun onResume() {
        super.onResume()
        getViewModelFactory()
        setupViewModel()
    }

    private fun setupViewModel() {
        weatherViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(WeatherViewModel::class.java)
    }

}
