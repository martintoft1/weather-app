package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.data.models.VectorTile

interface ApiService {

    fun getWeather(): List<VectorDataset>

    /*
    fun getAirTemp(): MutableList<VectorTile>

    fun getClouds(): MutableList<VectorTile>

    fun getPrecipitation(): MutableList<VectorTile>

    fun getPressure(): MutableList<VectorTile>
     */

}