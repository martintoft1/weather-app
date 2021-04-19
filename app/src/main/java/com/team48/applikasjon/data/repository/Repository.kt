package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class Repository (private val apiHelper: ApiHelper) {

    fun getWeather(): Single<List<Weather>> {
        return apiHelper.getWeather()
    }

    fun getAirTemp(): MutableList<VectorTile> {
        return apiHelper.getAirTemp()
    }

    fun getClouds(): MutableList<VectorTile> {
        return apiHelper.getClouds()
    }

    fun getPrecipitation(): MutableList<VectorTile> {
        return apiHelper.getPrecipitation()
    }

    fun getPressure(): MutableList<VectorTile> {
        return apiHelper.getPressure()
    }
}