package com.team48.applikasjon.data.api

class ApiHelper (private  val apiServiceImpl : ApiServiceImpl) {

   fun getWeather() = apiServiceImpl.getWeather()

   //fun refreshWeather() = apiServiceImpl.refreshWeather()

}