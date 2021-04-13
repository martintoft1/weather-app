package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.api.ApiService

class ApiHelper (private  val apiService : ApiService){
   //alle kall på data skjer her, repository kaller  igjen på ApiHelper, hvorfor det går gjennom så mange ledd
   // er uklart for øyeblikken, men kommer sikkert frem under utivikling/research

   fun getWeather()  = apiService.getWeather()
}