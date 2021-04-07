package com.team48.applikasjon.data.api

import com.team48.applikasjon.data.models.MetVectorData

interface ApiService {

    suspend fun getMetData(): List<MetVectorData>

}