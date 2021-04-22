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


}