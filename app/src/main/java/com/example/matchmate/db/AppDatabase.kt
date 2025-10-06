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
// 1. Define the entities (tables) and the database version.
//    Every time you change the schema (e.g., add a column), you must increment the version.
@Database(entities = [CardProfile::class, HistoryProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 2. Declare an abstract function for each DAO. Room will generate the implementation.
    abstract fun profileDao(): ProfileDao
    abstract fun historyProfileDao(): HistoryProfileDao


    // 3. Use a companion object to create a singleton instance of the database.
    //    This prevents having multiple instances of the database open at the same time.
    companion object {
        // @Volatile ensures that the value of INSTANCE is always up-to-date and the same for all execution threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the AppDatabase.
         * If an instance already exists, it returns it. Otherwise, it creates one.
         * This method is thread-safe.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's not null.
            return INSTANCE ?: synchronized(this) {
                // If the instance is null, create the database.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "matchmate_database" // The name of your database file.
                )
                    .build()
                INSTANCE = instance
                // Return the newly created instance.
                instance
            }
        }
    }
}
