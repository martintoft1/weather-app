package com.team48.applikasjon.ui.dailyweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.models.Weather
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.utils.StatusUI


class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private lateinit var rootView: View
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var weatherRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        //setContentView(R.layout.fragment_weather_view)
        super.onCreate(savedInstanceState)
    }

    private fun setupRecyclerView() {
        weatherViewModel = ViewModelProviders.of(
            this,
        ).get(WeatherViewModel::class.java)
        weatherAdapter = WeatherAdapter(mutableListOf())
        weatherRecyclerView = rootView.findViewById(R.id.recyclerView)
        weatherRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        weatherRecyclerView.adapter = weatherAdapter
    }

    private fun setupObservers() {
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar)
        weatherViewModel.getWeather().observe(viewLifecycleOwner, {
            when (it.status) {
                StatusUI.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    renderWeather(it.data!!)
                    weatherRecyclerView.visibility = View.VISIBLE
                }

                StatusUI.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    weatherRecyclerView.visibility = View.GONE
                }

                StatusUI.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun renderWeather(weatherList: List<Weather>) {
        weatherAdapter.addData(weatherList)
        weatherAdapter.notifyDataSetChanged()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_weather, container, false)
        // Inflate the layout for this fragment
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }
}
