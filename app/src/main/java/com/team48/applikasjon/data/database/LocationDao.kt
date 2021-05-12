package com.team48.applikasjon.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.team48.applikasjon.data.models.Location

/* DAO: Data Access Object med metoder som kommuniserer med Locations-databasen */

@Dao
interface LocationDao {

    @Query("SELECT * FROM table_location")
    fun getLocations() : LiveData<MutableList<Location>>

    @Query("SELECT EXISTS (SELECT * from table_location WHERE name = :name)")
    fun checkLocation(name: String) : LiveData<Boolean>

    @Insert
    suspend fun addLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT COUNT(*) FROM table_location")
    fun getCount() : LiveData<Int>

}