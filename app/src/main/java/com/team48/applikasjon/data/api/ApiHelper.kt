package com.team48.applikasjon.data.api

class ApiHelper (private  val apiServiceImpl : ApiServiceImpl) {

   fun getAirTemp() = apiServiceImpl.getAirTemp()

   fun getClouds() = apiServiceImpl.getClouds()

   fun getPrecipitation() = apiServiceImpl.getPrecipitation()

   fun getPressure() = apiServiceImpl.getPressure()

}