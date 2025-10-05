package com.example.matchmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profiles")
    suspend fun getAllProfiles(): List<UserProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<UserProfile>)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()

    @Query("SELECT uid FROM seen_profiles")
    suspend fun getAllSeenUids(): List<String>

    @Query("UPDATE user_profiles SET interactionStatus = :status WHERE uid = :uid")
    suspend fun updateInteractionStatus(uid: String, status: InteractionStatus)

}