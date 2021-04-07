package com.team48.applikasjon.data.repository

import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.models.MetVectorData

class Repository(private val apiHelper: ApiHelper) {

    suspend fun getMetData(): List<MetVectorData> {
        return apiHelper.getMetData()
    }


}