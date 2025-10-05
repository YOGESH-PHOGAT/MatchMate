package com.example.matchmate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchmate.db.ProfileRepository
import com.example.matchmate.data.UserProfile
import kotlinx.coroutines.launch

class UserViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profiles = MutableLiveData<List<UserProfile>>()
    val profiles: LiveData<List<UserProfile>> = _profiles

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var isFetching = false

    init {
        loadMoreProfiles()
    }

    /**
     * This is the main function to call for pagination.
     * It fetches the next batch of users from the network, adds them to the database,
     * and then reloads the complete list from the database to update the UI.
     * It's safe to call this multiple times (e.g., on scroll).
     */
    fun loadMoreProfiles() {
        // 3. Prevent new fetches if one is already in progress
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            _isLoading.value = true
            try {
                // 4. Call the repository to fetch the next batch and save it
                repository.fetchAndCacheNextBatch()

                // 5. After fetching, get the updated, complete list from the database
                val updatedProfileList = repository.getAllCachedProfiles()
                _profiles.value = updatedProfileList

            } catch (e: Exception) {
                _error.value = "Failed to fetch profiles: ${e.message}"
            } finally {
                // 6. Reset flags after the operation is complete
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun onForcedRefresh() {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            _isLoading.value = true
            try {
                // 1. Clear the entire local cache
                repository.clearAllProfiles()

                // 2. Fetch the first batch of fresh data
                repository.fetchAndCacheNextBatch()

                // 3. Get the new list from the database
                val refreshedList = repository.getAllCachedProfiles()
                _profiles.value = refreshedList

            } catch (e: Exception) {
                _error.value = "Failed to refresh profiles: ${e.message}"
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }


    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val profileList = repository.getAllCachedProfiles()
                _profiles.value = profileList
            } catch (e: Exception) {
                // The repository logs errors, but we can still post a generic one to the UI
                _error.value = "Failed to fetch profiles: ${e.message}"
            }
        }
    }
}

class UserViewModelFactory(private val repository: ProfileRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of type UserViewModel
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            // If it is, create and return an instance, passing the repository
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        // If it's not, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}