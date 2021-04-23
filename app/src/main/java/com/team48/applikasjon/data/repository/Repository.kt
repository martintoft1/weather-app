package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl

class Repository {

    private val apiHelper = ApiHelper(ApiServiceImpl())

    fun getAirTemp() = apiHelper.getAirTemp()

    fun getClouds() = apiHelper.getClouds()

    fun getPrecipitation() = apiHelper.getPrecipitation()

    fun getPressure() = apiHelper.getPressure()

}