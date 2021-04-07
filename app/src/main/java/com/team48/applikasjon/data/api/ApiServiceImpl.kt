package com.team48.applikasjon.data.api

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.team48.applikasjon.data.models.MetVectorData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiServiceImpl : ApiService {

    override suspend fun getMetData(): List<MetVectorData> {

        val metPath = "https://test.openmaps.met.no/in2000/map/services"
        var metData: List<MetVectorData> = emptyList()

        CoroutineScope(Dispatchers.IO).launch {

            try {
                metData = Gson().fromJson(Fuel.get(metPath).awaitString(), Array<MetVectorData>::class.java).toList()
            } catch (exception: Exception) {
                exception.message?.let { Log.e("getting MET-data", it) }
            }
        }

        return metData
    }
}