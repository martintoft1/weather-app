package com.team48.applikasjon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/* Modellen for databaseobjektene */

@Entity(tableName = "table_location")
data class LocationModel (
       @PrimaryKey(autoGenerate = true)
       val id: Int,
       var name: String,
       var cloud_percentage: Float?,
       var rain_mm: Float?,
       var temp_celsius: Float?,
       var latLong: String
) {

       /* Variabler som knytter instansen til favorittstrukturen */
       var expanded: Boolean = false
       var favourite: Boolean = false
}
