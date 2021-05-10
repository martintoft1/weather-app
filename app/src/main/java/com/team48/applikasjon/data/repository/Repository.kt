package com.team48.applikasjon.data.repository

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.team48.applikasjon.R
import com.team48.applikasjon.data.api.ApiHelper
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.ui.main.MainActivity

class Repository {

    private val apiHelper = ApiHelper(ApiServiceImpl())
    private val defaultStyle: String = R.string.mapStyleLight.toString()

    val modeStyle: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getWeather() = apiHelper.getWeather()

    /*
    fun setCustomMapStyle(style: String) {
        customMapStyle = style
    }
    fun getCustomMapStyle() = customMapStyle

     */

}