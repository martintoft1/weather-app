package com.team48.applikasjon.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.team48.applikasjon.data.models.LocationModel

/* DAO: Data Access Object med metoder som kommuniserer med Locations-databasen */

@Dao
interface LocationDao {

    @Query("SELECT * FROM table_location")
    fun getLocations() : LiveData<MutableList<LocationModel>>

    @Query("SELECT EXISTS (SELECT * from table_location WHERE name = :name)")
    fun checkLocation(name: String) : LiveData<Boolean>

    @Insert
    suspend fun addLocation(locationModel: LocationModel)

    @Delete
    suspend fun deleteLocation(locationModel: LocationModel)

    @Query("SELECT COUNT(*) FROM table_location")
    fun getCount() : LiveData<Int>

    @Query("DELETE FROM table_location")
    suspend fun clearDatabase()

}