package com.team48.applikasjon.data.repository

import androidx.lifecycle.MutableLiveData
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class Repository {

    private val apiHelper = ApiHelper(ApiServiceImpl())

    fun getAirTemp() = apiHelper.getAirTemp()

    fun getClouds() = apiHelper.getClouds()

    fun getPrecipitation() = apiHelper.getPrecipitation()

    fun getPressure() = apiHelper.getPressure()

}