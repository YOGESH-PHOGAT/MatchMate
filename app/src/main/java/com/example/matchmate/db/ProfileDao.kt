package com.example.matchmate.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profiles")
    suspend fun getAllProfiles(): List<CardProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<CardProfile>)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()

    @Query("SELECT uid FROM user_profiles")
    suspend fun getAllUids(): List<String>

    @Query("DELETE FROM user_profiles WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)
}

@Dao
interface HistoryProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: HistoryProfile)

    @Query("SELECT * FROM history_profiles")
    suspend fun getAllProfiles(): List<HistoryProfile>

}
