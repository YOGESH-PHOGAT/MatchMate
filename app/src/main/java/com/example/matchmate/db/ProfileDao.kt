package com.example.matchmate.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matchmate.data.UserProfile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profiles")
    suspend fun getAllProfiles(): List<UserProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<UserProfile>)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()

    @Query("SELECT uid FROM user_profiles")
    suspend fun getAllUids(): List<String>
}