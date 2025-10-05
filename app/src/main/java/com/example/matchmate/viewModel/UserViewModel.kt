package com.example.matchmate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchmate.data.Result
import com.example.matchmate.network.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {

    private val _profiles = MutableLiveData<List<Result>>()
    val profiles: LiveData<List<Result>> = _profiles

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val service = RetrofitClient.apiService
                val response = service.getProfiles(10)

                if (response.isSuccessful) {
                    val userResponse = response.body()
                    _profiles.value = userResponse?.results
                } else {
                    // Handle API error (e.g., show an error message)
                    _error.value = "API Error: ${response.code()}"
                }
            } catch (e: Exception) {
                // Handle network failure (e.g., no internet connection)
                _error.value = "Network Error: ${e.message}"
            }
        }
    }
}