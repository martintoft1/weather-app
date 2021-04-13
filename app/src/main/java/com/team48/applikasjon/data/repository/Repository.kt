package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class Repository (private val apiHelper: ApiHelper) {
    
    fun getWeather(): Single<List<Weather>>  {
        return apiHelper.getWeather()
    }
}