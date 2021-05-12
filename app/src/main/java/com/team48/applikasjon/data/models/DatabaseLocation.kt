package com.team48.applikasjon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_location")
data class DatabaseLocation (
       @PrimaryKey(autoGenerate = true)
       val id: Int,
       var name: String,
       var cloud_percentage: Float?,
       var rain_mm: Float?,
       var temp_celsius: Float?,
       var lat: Double,
       var long: Double
) {
       var expanded: Boolean = false
       var favourite: Boolean = false
}
