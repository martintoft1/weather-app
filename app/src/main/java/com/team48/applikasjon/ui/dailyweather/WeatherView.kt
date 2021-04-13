package com.team48.applikasjon.ui.dailyweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.team48.applikasjon.R



class WeatherView : Fragment(R.layout.fragment_weather_view) {

    var weatherView: WeatherView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //setContentView(R.layout.fragment_weather_view)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //weatherView = view.findViewById(R.id.weather)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        weatherView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        weatherView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        weatherView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        weatherView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        weatherView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        weatherView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        weatherView?.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        weatherView?.onDestroy()
    }
}
