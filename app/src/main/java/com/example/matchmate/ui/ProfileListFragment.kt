package com.example.matchmate.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmate.AdapterItem
import com.example.matchmate.ItemClickListener
import com.example.matchmate.ProfileAdapter
import com.example.matchmate.R
import com.example.matchmate.databinding.ProfileListLayoutBinding
import com.example.matchmate.db.AppDatabase
import com.example.matchmate.db.ProfileRepository
import com.example.matchmate.db.UserProfile
import com.example.matchmate.network.RetrofitClient
import com.example.matchmate.viewModel.UserViewModel
import com.example.matchmate.viewModel.UserViewModelFactory
import kotlin.getValue

const val HISTORY_FRAGMENT_TAG = "HISTORY_FRAGMENT_TAG"
const val PROFILE_LIST_FRAGMENT_TAG = "PROFILE_LIST_FRAGMENT_TAG"


class ProfileListFragment: Fragment(), ItemClickListener {

    private var _binding: ProfileListLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileAdapter: ProfileAdapter

    companion object {

        const val HISTORY_FRAGMENT = "HISTORY_FRAGMENT"

        fun newInstance(isHistory: Boolean) = ProfileListFragment().apply {
            arguments = bundleOf(HISTORY_FRAGMENT to isHistory)
        }
    }

    private var isHistory = false
    private var adapterItemList: ArrayList<AdapterItem> = arrayListOf()



    private val userViewModel: UserViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = ProfileRepository(RetrofitClient.apiService, database)
        UserViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileListLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MainActivity", "fragment onViewCreated: Starting")
        isHistory = arguments?.getBoolean(HISTORY_FRAGMENT) ?: false
        initViews(isHistory)
        setupRecyclerView()
        observeViewModel(isHistory)
        if (isHistory) {
            userViewModel.fetchProfilesFromHistory()
        } else {
            userViewModel.fetchUsers()
        }
    }

    private fun initViews(isHistory: Boolean) {
        with(binding) {
            if (isHistory) {
                tvTitle.text = getString(R.string.history)
                tvHistory.visibility = View.INVISIBLE
                ivClose.visibility = View.VISIBLE
                ivClose.setOnClickListener {
                    parentFragmentManager.beginTransaction().remove(this@ProfileListFragment).commit()
                }
            } else {
                tvTitle.text = getString(R.string.profile_matches)
                tvHistory.visibility = View.VISIBLE
                ivClose.visibility = View.INVISIBLE
                tvHistory.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .add(R.id.flContainer, newInstance(true), HISTORY_FRAGMENT_TAG).commit()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter(this)
        binding.rvProfiles.adapter = profileAdapter
        binding.rvProfiles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val isLoading = userViewModel.isLoading.value ?: false
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (isHistory) {
                        userViewModel.loadMoreProfilesFromHistory()
                    } else {
                        userViewModel.loadMoreProfiles()
                    }
                }
            }
        })
    }

    private fun observeViewModel(isHistory: Boolean) {
        if (isHistory) {
            userViewModel.historyProfiles.observe(viewLifecycleOwner) { profiles ->
                if (profiles.isNotEmpty()) {
                    binding.tvNothingHere.visibility = View.GONE
                    adapterItemList = arrayListOf<AdapterItem>()
                    profiles.forEach {
                        adapterItemList.add(AdapterItem.ProfileItem(it))
                    }
                    adapterItemList.add(AdapterItem.LoadingItem)
                    profileAdapter.submitList(adapterItemList)
                    Log.d("MainActivity", "API Call Success: Received ${profiles.size} history profiles.")
                } else {
                    binding.tvNothingHere.visibility = View.VISIBLE
                    Log.d("MainActivity", "API Call Success: Received an empty list of history profiles.")
                }
            }
        } else {
            userViewModel.profiles.observe(viewLifecycleOwner) { profiles ->
                if (profiles.isNotEmpty()) {
                    binding.tvNothingHere.visibility = View.GONE
                    adapterItemList = arrayListOf()
                    profiles.forEach {
                        adapterItemList.add(AdapterItem.ProfileItem(it))
                    }
                    adapterItemList.add(AdapterItem.LoadingItem)
                    Log.d("MainActivity", "API Call Success: Received ${profiles.size} profiles.")
                    profileAdapter.submitList(adapterItemList)

                } else {
                    binding.tvNothingHere.visibility = View.VISIBLE
                    Log.d("MainActivity", "API Call Success: Received an empty list.")
                }
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("MainActivity", "API Call Error: $errorMessage")
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    override fun onProfileClicked(profile: UserProfile) {
        // profile is seen now viewmodel would move it in history and update the list.
        userViewModel.onProfileClicked(profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}