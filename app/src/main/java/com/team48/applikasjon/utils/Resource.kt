package com.team48.applikasjon.utils

data class Resource<out T>(val status: StatusUI, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(StatusUI.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(StatusUI.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(StatusUI.LOADING, data, null)
        }

    }

}
