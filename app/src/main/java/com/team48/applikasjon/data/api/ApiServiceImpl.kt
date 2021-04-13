package com.team48.applikasjon.data.api

import com.rx2androidnetworking.Rx2AndroidNetworking
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

class ApiServiceImpl : ApiService {

    override fun getWeather(): Single<List<Weather>> {
        return Rx2AndroidNetworking.get("https://5e510330f2c0d300147c034c.mockapi.io/users")
            .build()
            .getObjectListSingle(Weather::class.java)
    }

}