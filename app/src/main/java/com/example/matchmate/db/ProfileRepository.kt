package com.example.matchmate.db

import android.util.Log
import androidx.room.withTransaction
import com.example.matchmate.data.toEntity
import com.example.matchmate.network.ApiService

/**
 * Repository module for handling data operations.
 * It provides a clean API for the rest of the app to interact with the data layer.
 * It's responsible for fetching data from the network and storing it in the local database.
 *
 * @param apiService The service for making network requests.
 * @param profileDao The Data Access Object for interacting with the local database.
 */
class ProfileRepository(
    private val apiService: ApiService,
    private val db: AppDatabase
) {


    private val profileDao = db.profileDao()
    private val historyProfileDao = db.historyProfileDao()

    /**
     * Fetches a new batch of profiles from the network, filters out any duplicates
     * that are already in the database, and then saves the new unique profiles.
     * This is the core function for pagination.
     *
     * @param batchSize The number of profiles to request from the API.
     */
    suspend fun fetchAndCacheNextBatch(batchSize: Int = 20) {
        Log.d("ProfileRepository", "Attempting to fetch next batch of $batchSize profiles.")
        try {
            // 1. Get a list of all profile IDs currently in our database for efficient filtering.
            val existingUids = profileDao.getAllUids().toSet().plus(historyProfileDao.getAllUids())

            Log.d("ProfileRepository", "Found ${existingUids.size} existing profiles in database.")

            // 2. Make the network call to get a new batch of profiles.
            val response = apiService.getProfiles(results = batchSize)
            Log.d("ProfileRepository","API Call Received ${response.body()} ")

            if (response.isSuccessful) {
                val profilesFromApi = response.body()?.results ?: emptyList()

                if (profilesFromApi.isNotEmpty()) {
                    // 3. Filter out profiles that we already have in our database.
                    val newProfiles = profilesFromApi
                        .map { it.toEntity() } // Convert network models to database entities
                        .filter { profile -> !existingUids.contains(profile.uid) } // The important filter step

                    // 4. If we found any new profiles, append them to the database.
                    if (newProfiles.isNotEmpty()) {
                        profileDao.insertAll(newProfiles)
                        Log.d("ProfileRepository", "SUCCESS: Appended ${newProfiles.size} new profiles to the database.")
                    } else {
                        Log.w("ProfileRepository", "WARN: The API returned profiles that are already in the database. No new profiles were added.")
                    }
                } else {
                    Log.w("ProfileRepository", "WARN: API response was successful but contained no profiles.")
                }
            } else {
                // Handle non-2xx HTTP responses
                Log.e("ProfileRepository", "API Error: Received code ${response.code()} with message: ${response.message()}")
            }
        } catch (e: Exception) {
            // Handle network failures (e.g., no internet connection) or other exceptions.
            Log.e("ProfileRepository", "Network Failure: Could not fetch profiles. ${e.message}")
        }
    }

    /**
     * Retrieves all profiles currently stored in the local database.
     * The ViewModel will call this to get the data to display.
     *
     * @return A list of all UserProfile entities from the database.
     */
    suspend fun getAllCachedProfiles(): List<CardProfile> {
        Log.d("ProfileRepository", "Retrieving all profiles from local cache.")
        val res = profileDao.getAllProfiles()
        if (res.isEmpty()) {
            fetchAndCacheNextBatch()
            return profileDao.getAllProfiles()
        } else {
            return res
        }
    }

    suspend fun getHistoryProfiles(): List<HistoryProfile> {
        Log.d("ProfileRepository", "Retrieving all history profiles from local cache.")
        return historyProfileDao.getAllProfiles()
    }

    /**
     * Clears the entire 'user_profiles' table.
     * This is useful for implementing a "pull-to-refresh" feature where you want to
     * discard all old data and start fresh.
     */
    suspend fun clearAllProfiles() {
        Log.d("ProfileRepository", "Clearing all profiles from the database.")
        profileDao.deleteAll()
    }

    suspend fun moveProfileToHistory(userProfile: UserProfile) {
        val historyProfile = HistoryProfile(
            uid = userProfile.uid,
            gender = userProfile.gender,
            firstName = userProfile.firstName,
            lastName = userProfile.lastName,
            city = userProfile.city,
            state = userProfile.state,
            phone = userProfile.phone,
            cell = userProfile.cell,
            pictureLarge = userProfile.pictureLarge,
            pictureMedium = userProfile.pictureMedium,
            pictureThumbnail = userProfile.pictureThumbnail,
            interactionStatus = userProfile.interactionStatus
        )

        db.withTransaction {
            historyProfileDao.insert(historyProfile)
            profileDao.deleteByUid(userProfile.uid)
        }
    }
}
