package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class Repository (private val apiHelper: ApiHelper) {

    fun getWeather(): Single<List<Weather>> {
        return apiHelper.getWeather()
    }

    fun getAirTemp(): Single<List<VectorTile>> {
        return apiHelper.getAirTemp()
    }

    fun getClouds(): Single<List<VectorTile>> {
        return apiHelper.getClouds()
    }

    fun getPrecipitation(): Single<List<VectorTile>> {
        return apiHelper.getPrecipitation()
    }

    fun getPressure(): Single<List<VectorTile>> {
        return apiHelper.getPressure()
    }
}