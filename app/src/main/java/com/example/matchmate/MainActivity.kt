package com.example.matchmate

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmate.db.ProfileRepository
import com.example.matchmate.databinding.ActivityMainBinding
import com.example.matchmate.db.AppDatabase
import com.example.matchmate.network.RetrofitClient
import com.example.matchmate.viewModel.UserViewModel
import com.example.matchmate.viewModel.UserViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var profileAdapter: ProfileAdapter

    private val userViewModel: UserViewModel by viewModels {
        // This block is responsible for creating the ViewModel's dependencies.
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProfileRepository(RetrofitClient.apiService, database.profileDao())
        UserViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        observeViewModel()

        // 3. Trigger the API call
        // This is safe to call from onCreate because the coroutine
        // inside the ViewModel will run on a background thread.
        userViewModel.fetchUsers()
    }
    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter()
        binding.rvProfiles.adapter = profileAdapter
        binding.rvProfiles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Check if we should load more data
                val isLoading = userViewModel.isLoading.value ?: false
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    Log.d("MainActivity", "Scroll listener: Reached end of list, loading more.")
                    userViewModel.loadMoreProfiles()
                }
            }
        })
    }

    private fun observeViewModel() {
        userViewModel.profiles.observe(this) { profiles ->
            // This block will execute whenever the profile list is successfully updated.
            if (profiles.isNotEmpty()) {

                val adapterItemList = profiles.map { AdapterItem.ProfileItem(it) }

                Log.d("MainActivity", "API Call Success: Received ${profiles.size} profiles.")
                // Log the name of the first user as an example

                profileAdapter.submitList(adapterItemList)

                Log.d("MainActivity", "First user: ${profiles[0].firstName} ${profiles[0].gender}")
            } else {
                Log.d("MainActivity", "API Call Success: Received an empty list.")
            }
        }

        // Observe the 'error' LiveData
        userViewModel.error.observe(this) { errorMessage ->
            // This block will execute if there is an API or network error.
            Log.e("MainActivity", "API Call Error: $errorMessage")
        }
    }
}

