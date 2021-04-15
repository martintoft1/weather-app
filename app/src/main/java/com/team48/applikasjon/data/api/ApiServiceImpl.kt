package com.team48.applikasjon.data.api

import com.rx2androidnetworking.Rx2AndroidNetworking
import com.team48.applikasjon.data.models.Weather
import io.reactivex.Single

// Klarte ikke opprette instans av ApiService direkte, så kanskje er ikke denne klassen helt ubrukelig likevel? Idk

class ApiServiceImpl : ApiService {

    override fun getWeather(): Single<List<Weather>> {
        return Rx2AndroidNetworking.get("https://run.mocky.io/v3/7f30d519-4597-48b5-8abf-3da7b0a9063f")
            .build()
            .getObjectListSingle(Weather::class.java)
    }

}