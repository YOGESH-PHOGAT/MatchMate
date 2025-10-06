package com.example.matchmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.matchmate.databinding.ActivityMainBinding
import com.example.matchmate.ui.PROFILE_LIST_FRAGMENT_TAG
import com.example.matchmate.ui.ProfileListFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            initFragment()
        }
    }

    private fun initFragment() {
        val profileFragment = ProfileListFragment.newInstance(false)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.flContainer.id, profileFragment, PROFILE_LIST_FRAGMENT_TAG)
        fragmentTransaction.commit()
    }
}

