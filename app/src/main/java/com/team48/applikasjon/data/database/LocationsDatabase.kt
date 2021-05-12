package com.team48.applikasjon.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team48.applikasjon.data.models.Location


@Database(entities = [Location::class], version = 3)
abstract class LocationsDatabase : RoomDatabase() {

    abstract fun locationDao() : LocationDao

    companion object {
        @Volatile // Visible to other threads
        private var INSTANCE: LocationsDatabase? = null

        fun getDatabase(context: Context): LocationsDatabase {

            // Database instance exists
            if (INSTANCE!= null) {
                return INSTANCE as LocationsDatabase
            }

            // Create new instance of database
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    LocationsDatabase::class.java,
                    "main_database"
                ).build()
                return INSTANCE as LocationsDatabase
            }
        }
    }
}