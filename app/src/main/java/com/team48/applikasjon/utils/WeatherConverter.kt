package com.team48.applikasjon.utils

/* Konvertering av værdata til forhåndsbestemte beskrivelser */
class WeatherConverter {
    private val cloudDescs = listOf("Ingen skyer", "Delvis skyet", "Overskyet")
    private val rainDescs  = listOf("Ingen nedbør", "Oppholdsvær", "Pjuskeregn","Regn", "Pøsregn!")
    private val tempDescs  = listOf("Iskaldt!", "Kjølig", "Lunkent", "Godt og varmt", "Kokvarmt!")

    // Henter beskrivelse for skydekke
    fun getCloudDesc(value: Float?) : String {
        if (value == null) return cloudDescs[0]

        return when {
            value <= 30 -> cloudDescs[0]
            value <= 60 -> cloudDescs[1]
            else        -> cloudDescs[2]
        }
    }

    // Henter beskrivelse for nedbør
    fun getRainDesc(value: Float?) : String {
        if (value == null) return rainDescs[0] + " ($value mm)"

        return when {
            value <= 0.0 -> rainDescs[0] + " ($value mm)"
            value <= 0.2 -> rainDescs[1] + " ($value mm)"
            value <= 0.8 -> rainDescs[2] + " ($value mm)"
            value <= 1.5 -> rainDescs[3] + " ($value mm)"
            else         -> rainDescs[4] + " ($value mm)"
        }
    }

    // Henter beskrivelse for temperatur
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

    // Henter alle beskrivelsene
    fun getWeatherDesc(cloud: Float?, rain: Float?, temp: Float?) : String {

        /* Sørger for å ha verdier å sammenlikne selv om noen av verdiene skulle være 0
         * Beholder oppsett for skydekke for videre utvikling */

        //val cloudVar = if (cloud == null) 0.toFloat() else cloud
        val rainVar = if (rain == null) 0.toFloat() else rain
        val tempVar = if (temp == null) 0.toFloat() else temp

        return when {
            rainVar > 0.4 && tempVar < 10 && tempVar >= -2 -> "Varmt utetøy som tåler vann, og varme støvler"
            rainVar > 0.4 && tempVar >= 10 -> "Regntøy og støvler"
            tempVar < -5 -> "Skikkelig varmt utetøy"
            tempVar < 5  -> "Varmt utetøy"
            tempVar < 15 -> "Jakke, lue, skjerf og hansker"
            tempVar < 20 -> "Genser og bukse"
            tempVar < 25 -> "Bukse og t-skjorte"

            else -> "Shorts og t-skjorte"
        }
    }
}