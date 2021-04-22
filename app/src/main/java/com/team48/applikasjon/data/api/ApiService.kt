package com.team48.applikasjon.data.api

import androidx.lifecycle.MutableLiveData
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single
import com.team48.applikasjon.data.models.VectorTile

interface ApiService {

    fun getAirTemp(): MutableList<VectorTile>

    fun getClouds(): MutableList<VectorTile>

    fun getPrecipitation(): MutableList<VectorTile>

    fun getPressure(): MutableList<VectorTile>

}