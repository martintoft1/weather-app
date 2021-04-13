package com.team48.applikasjon.data.api

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.gson.Gson
import android.widget.*
import com.rx2androidnetworking.Rx2AndroidNetworking

interface ApiService {

    fun getWeather(): Single<List<Weather>>
}
    /*
    fun getWeather(): MutableList<Weather> {
        var path = "http://localhost:3000/weather"
        var listOfWeather = mutableListOf<Weather>()
        val gson = Gson()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = Fuel.get(path).awaitString()
                val response = gson.fromJson(data, Weather::class.java)
                Log.d("API fetching", response.toString())

                listOfWeather.add(response)

            } catch (exception: Exception) {
                println("A network request exception was thrown; ${exception.message}")
            }
        }
        return listOfWeather
    }*/


