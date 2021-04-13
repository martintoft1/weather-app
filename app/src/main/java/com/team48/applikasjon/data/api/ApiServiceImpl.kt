package com.team48.applikasjon.data.api

import com.rx2androidnetworking.Rx2AndroidNetworking
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

// Klarte ikke opprette instans av ApiService direkte, så kanskje er ikke denne klassen helt ubrukelig likevel? Idk

class ApiServiceImpl : ApiService {

    override fun getWeather(): Single<List<Weather>> {
                                        // Denne stemmer vel ikke helt eeee?
        return Rx2AndroidNetworking.get("https://5e510330f2c0d300147c034c.mockapi.io/users")
            .build()
            .getObjectListSingle(Weather::class.java)
    }

}