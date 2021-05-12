package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.VectorDataset

interface ApiService {

    fun getWeather(): List<VectorDataset>

}