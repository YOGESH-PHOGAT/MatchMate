package com.example.matchmate.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchmate.db.CardProfile
import com.example.matchmate.db.HistoryProfile
import com.example.matchmate.db.ProfileRepository
import com.example.matchmate.db.UserProfile
import kotlinx.coroutines.launch

class UserViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profiles = MutableLiveData<List<CardProfile>>()
    val profiles: LiveData<List<CardProfile>> = _profiles

    private val _historyProfiles = MutableLiveData<List<HistoryProfile>>()
    val historyProfiles: LiveData<List<HistoryProfile>> = _historyProfiles


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isPagination = MutableLiveData<Boolean>()

    val isPagination: LiveData<Boolean> = _isPagination


    private var isFetching = false


    /**
     * This is the main function to call for pagination.
     * It fetches the next batch of users from the network, adds them to the database,
     * and then reloads the complete list from the database to update the UI.
     */
    fun loadMoreProfiles() {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            _isPagination.value = true
            try {
                repository.fetchAndCacheNextBatch()
                val updatedProfileList = repository.getAllCachedProfiles()
                _profiles.value = updatedProfileList

            } catch (e: Exception) {
                _error.value = "Failed to fetch profiles: ${e.message}"
            } finally {
                isFetching = false
                _isPagination.value = false
            }
        }
    }

    fun fetchProfilesFromHistory() {

        viewModelScope.launch {
            isFetching = true
            _isLoading.value = true
            try {
                _historyProfiles.value = repository.getHistoryProfiles()
            } catch (e: Exception) {
                _error.value = "Failed to load history: ${e.message}"
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }

    }

    fun loadMoreProfilesFromHistory() {
        // similar pagination as profile.
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


    fun fetchUsers(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _isLoading.value = true
            }
            try {
                val profileList = repository.getAllCachedProfiles()
                _profiles.value = profileList
            } catch (e: Exception) {
                // The repository logs errors, but we can still post a generic one to the UI
                _error.value = "Failed to fetch profiles: ${e.message}"
            } finally {
                if (showLoading) {
                    _isLoading.value = false
                }
            }
        }
    }

    fun onProfileClicked(profile: UserProfile) {
        if (profile !is CardProfile) return

        viewModelScope.launch {
            try {
                repository.moveProfileToHistory(profile)
                fetchUsers(false)

            } catch (e: Exception) {
                _error.value = "Failed to move profile to history: ${e.message}"
            }
        }

        /**
         * The repo changes of history would also be synced with backend.
         * I would try batching the responses to ensure routine updates and avoid constant api calls.
         * To ensure data preservation, the app would also perform a sweeping api call to upload all responses just before exit.
         * In routine cases, data losses should be minimal, but without graceful exits there would be lost responses.
         *
         */
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