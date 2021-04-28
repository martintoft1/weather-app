package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl

class Repository {

    private val apiHelper = ApiHelper(ApiServiceImpl())

    suspend fun getWeather() = apiHelper.getWeather()

    //fun refreshWeather() = apiHelper.refreshWeather()

}