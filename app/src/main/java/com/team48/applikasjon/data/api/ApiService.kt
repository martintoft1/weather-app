package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single
import com.team48.applikasjon.data.models.VectorTile

interface ApiService {

    fun getWeather(): Single<List<Weather>>

    fun getAirTemp(): MutableList<VectorTile>

    fun getClouds(): MutableList<VectorTile>

    fun getPrecipitation(): MutableList<VectorTile>

    fun getPressure(): MutableList<VectorTile>

}