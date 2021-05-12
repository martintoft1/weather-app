package com.team48.applikasjon.utils

class WeatherConverter() {
    val cloudDescs = listOf("Ingen skyer", "Delvis skyet", "Overskyet")
    val rainDescs  = listOf("Ingen nedbør", "Litt regn", "Mye regn")
    val tempDescs  = listOf("Iskaldt!", "Kjølig", "Godt og varmt")


    fun getCloudDesc(value: Float?) : String {
        if (value == null) return cloudDescs[0]

        return when {
            value <= 30 -> cloudDescs[0]
            value <= 60 -> cloudDescs[1]
            else        -> cloudDescs[2]
        }
    }


    fun getRainDesc(value: Float?) : String {
        if (value == null) return rainDescs[0] + " ($value mm)"

        return when {
            value <= 0.0 -> rainDescs[0] + " ($value mm)"
            value <= 0.5 -> rainDescs[1] + " ($value mm)"
            else         -> rainDescs[2] + " ($value mm)"
        }
    }


    fun getTempDesc(value: Float?) : String {
        if (value == null) return tempDescs[0] + " ($value °C)"

        return when {
            value <= 0.0 -> tempDescs[0] + " ($value °C)"
            value <= 10  -> tempDescs[1] + " ($value °C)"
            else         -> tempDescs[2] + " ($value °C)"
        }
    }
}