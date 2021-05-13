package com.team48.applikasjon.utils

class WeatherConverter() {
    val cloudDescs = listOf("Ingen skyer", "Delvis skyet", "Overskyet")
    val rainDescs  = listOf("Ingen nedbør", "Oppholdsvær", "Regn", "Pøsregn!")
    val tempDescs  = listOf("Iskaldt!", "Kjølig", "Lunkent", "Godt og varmt", "Kokvarmt!")


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
            value <= 0.8 -> rainDescs[1] + " ($value mm)"
            value <= 1 -> rainDescs[2] + " ($value mm)"
            else         -> rainDescs[3] + " ($value mm)"
        }
    }


    fun getTempDesc(value: Float?) : String {
        if (value == null) return tempDescs[0] + " ($value °C)"

        return when {
            value < -5.0 -> tempDescs[0] + " ($value °C)"
            value < 10.0 -> tempDescs[1] + " ($value °C)"
            value < 20   -> tempDescs[2] + " ($value °C)"
            value < 30   -> tempDescs[3] + " ($value °C)"
            else         -> tempDescs[4] + " ($value °C)"
        }
    }

    fun getWeatherDesc(cloud: Float?, rain: Float?, temp: Float?) : String {
        // Sørger for å ha verdier å sammenlikne selv om noen av verdiene skulle være 0
        val cloudVar: Float
        val rainVar: Float
        val tempVar: Float

        cloudVar = if (cloud == null) 0.toFloat() else cloud
        rainVar = if (rain == null) 0.toFloat() else rain
        tempVar = if (temp == null) 0.toFloat() else temp

        return when {
            rainVar > 0.4 && tempVar < 10 && tempVar >= -2 -> "Klesanbefaling: varmt utetøy som tåler vann, og varme støvler"
            rainVar > 0.4 && tempVar >= 10 -> "Klesanbefaling: regntøy og støvler"
            tempVar < -5 -> "Klesanbefaling: skikkelig varmt utetøy"
            tempVar < 5 -> "Klesanbefaling: varmt utetøy"
            tempVar < 15 -> "Klesanbefaling: jakke, lue, skjerf og hansker"
            tempVar < 20 -> "Klesanbefaling: genser og bukse"
            tempVar < 25 -> "Klesanbefaling: bukse og t-skjorte"

            else -> "Klesanbefaling: short og t-skjorte"
        }
    }
}