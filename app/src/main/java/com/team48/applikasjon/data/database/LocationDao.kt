package com.team48.applikasjon.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.team48.applikasjon.data.models.Location

/* DAO: Data Access Object med metoder som kommuniserer med Locations-databasen */

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getLocations() : LiveData<List<Location>>

    @Insert
    suspend fun addLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

}