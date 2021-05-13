package com.team48.applikasjon.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.team48.applikasjon.data.models.DatabaseLocation

/* DAO: Data Access Object med metoder som kommuniserer med Locations-databasen */

@Dao
interface LocationDao {

    @Query("SELECT * FROM table_location")
    fun getLocations() : LiveData<MutableList<DatabaseLocation>>

    @Query("SELECT EXISTS (SELECT * from table_location WHERE name = :name)")
    fun checkLocation(name: String) : LiveData<Boolean>

    @Insert
    suspend fun addLocation(databaseLocation: DatabaseLocation)

    @Delete
    suspend fun deleteLocation(databaseLocation: DatabaseLocation)

    @Query("SELECT COUNT(*) FROM table_location")
    fun getCount() : LiveData<Int>

    @Query("DELETE FROM table_location")
    suspend fun clearDatabase()

}