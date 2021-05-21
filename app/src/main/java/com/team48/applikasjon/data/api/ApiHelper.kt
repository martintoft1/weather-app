package com.team48.applikasjon.data.api

// En del av MVVM-strukturen. Lite nyttig for mindre prosjekter.
class ApiHelper (private  val apiServiceImpl : ApiServiceImpl) {

   fun getWeather() = apiServiceImpl.getWeather()

}