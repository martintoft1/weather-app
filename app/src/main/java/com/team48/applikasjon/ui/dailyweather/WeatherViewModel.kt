package com.team48.applikasjon.ui.dailyweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team48.applikasjon.data.models.Weather
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WeatherViewModel(private val repository: Repository) : ViewModel() {

    private val weatherList = MutableLiveData<Resource<List<Weather>>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        weatherList.postValue(Resource.loading(null))
        compositeDisposable.add(
            repository.getWeather()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userList ->
                    weatherList.postValue(Resource.success(userList))
                }, {
                    weatherList.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getWeather(): LiveData<Resource<List<Weather>>> {
        return weatherList
    }

}