package com.example.matchmate

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.matchmate.viewModel.UserViewModel

class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        observeViewModel()

        // 3. Trigger the API call
        // This is safe to call from onCreate because the coroutine
        // inside the ViewModel will run on a background thread.
        userViewModel.fetchUsers()
    }

    private fun observeViewModel() {
        userViewModel.profiles.observe(this) { profiles ->
            // This block will execute whenever the profile list is successfully updated.
            if (profiles.isNotEmpty()) {
                Log.d("MainActivity", "API Call Success: Received ${profiles.size} profiles.")
                // Log the name of the first user as an example
                Log.d("MainActivity", "First user: ${profiles[0].email} ${profiles[0].gender}")
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

