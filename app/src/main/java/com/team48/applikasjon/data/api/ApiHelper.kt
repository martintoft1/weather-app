package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.VectorTile
import io.reactivex.Single

class ApiHelper (private  val apiServiceImpl : ApiServiceImpl) {

   // alle kall på data skjer her, repository kaller  igjen på ApiHelper, hvorfor det går gjennom så mange ledd
   // er uklart for øyeblikken, men kommer sikkert frem under utivikling/research

   fun getWeather() = apiServiceImpl.getWeather()

   fun getAirTemp() = apiServiceImpl.getAirTemp()

   fun getClouds() = apiServiceImpl.getClouds()

   fun getPrecipitation() = apiServiceImpl.getPrecipitation()

   fun getPressure() = apiServiceImpl.getPressure()

}