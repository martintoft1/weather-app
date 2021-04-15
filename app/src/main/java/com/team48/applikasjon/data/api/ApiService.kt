package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single
import com.team48.applikasjon.data.models.VectorTile

interface ApiService {

    fun getWeather(): Single<List<Weather>>

    fun getAirTemp(): Single<List<VectorTile>>

    fun getClouds(): Single<List<VectorTile>>

    fun getPrecipitation(): Single<List<VectorTile>>

    fun getPressure(): Single<List<VectorTile>>

}