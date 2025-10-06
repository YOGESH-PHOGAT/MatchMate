package com.example.matchmate.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for this app.
 *
 * This class defines the database configuration and serves as the main access point
 * to the persisted data. It lists the entities that the database contains and the
 * DAOs (Data Access Objects) that it uses.
 */
@Database(entities = [CardProfile::class, HistoryProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun historyProfileDao(): HistoryProfileDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the AppDatabase.
         * If an instance already exists, it returns it. Otherwise, it creates one.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "matchmate_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
