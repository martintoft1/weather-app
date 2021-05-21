package com.team48.applikasjon.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team48.applikasjon.data.models.LocationModel

@Database(entities = [LocationModel::class], version = 1)
abstract class LocationsDatabase : RoomDatabase() {

    abstract fun locationDao() : LocationDao

    companion object {
        @Volatile // Synlig for andre tråder
        private var INSTANCE: LocationsDatabase? = null

        fun getDatabase(context: Context): LocationsDatabase {

            // Hvis databaseinstansen eksiterer
            if (INSTANCE!= null) {
                return INSTANCE as LocationsDatabase
            }

            // Hvis ikke opprettes en ny instans
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