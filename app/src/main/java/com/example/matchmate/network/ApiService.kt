package com.example.matchmate.network

import com.example.matchmate.data.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(GET_API)
    suspend fun getProfiles(
        @Query(QUERY_RESULTS) results: Int
    ): Response<UserResponse>
}