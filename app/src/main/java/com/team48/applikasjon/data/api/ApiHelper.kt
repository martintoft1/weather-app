package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.VectorTile
import io.reactivex.Single

class ApiHelper (private  val apiServiceImpl : ApiServiceImpl) {

   fun getAirTemp() = apiServiceImpl.getAirTemp()

   fun getClouds() = apiServiceImpl.getClouds()

   fun getPrecipitation() = apiServiceImpl.getPrecipitation()

   fun getPressure() = apiServiceImpl.getPressure()

}