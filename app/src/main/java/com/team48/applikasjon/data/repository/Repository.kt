package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class Repository () {

    val apiHelper = ApiHelper(ApiServiceImpl())

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