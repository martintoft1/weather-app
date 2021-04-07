package com.team48.applikasjon.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getMetData() = apiService.getMetData()

}