package com.team48.applikasjon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location (
       @PrimaryKey(autoGenerate = true)
       val id: Int,
       var name: String,
       var cloud_percentage: Float?,
       var rain_mm: Float?,
       var temp_celsius: Float?
) {
       var expanded: Boolean = false
}
