package com.example.matchmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.matchmate.data.InteractionStatus
import com.example.matchmate.databinding.LayoutLoadingItemBinding
import com.example.matchmate.databinding.LayoutProfileCardBinding
import com.example.matchmate.db.UserProfile

private const val ITEM_VIEW_TYPE_PROFILE = 0
private const val ITEM_VIEW_TYPE_LOADING = 1

class ProfileAdapter(val listener: ItemClickListener) : ListAdapter<AdapterItem, RecyclerView.ViewHolder>(ProfileDiffCallback()) {


    inner class ProfileViewHolder(val binding: LayoutProfileCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: UserProfile) {
            binding.apply {


                when (profile.interactionStatus) {
                    InteractionStatus.ACCEPTED -> {
                        btnAccept.visibility = View.INVISIBLE
                        btnReject.visibility = View.INVISIBLE
                        cardRoot.background = AppCompatResources.getDrawable(itemView.context, R.color.green_select)
                    }
                    InteractionStatus.DECLINED -> {
                        btnAccept.visibility = View.INVISIBLE
                        btnReject.visibility = View.INVISIBLE
                        cardRoot.background = AppCompatResources.getDrawable(itemView.context, R.color.red_reject)
                    }
                    InteractionStatus.UNSEEN -> {
                        btnAccept.visibility = View.VISIBLE
                        btnReject.visibility = View.VISIBLE
                        cardRoot.background = AppCompatResources.getDrawable(itemView.context, R.color.white)
                    }
                }

                tvName.text = "${profile.firstName} ${profile.lastName}"
                tvAddress.text = "${profile.city}, ${profile.state}"

                Glide.with(itemView.context)
                    .load(profile.pictureLarge)
                    .circleCrop() // Optional: Makes the image circular
                    .placeholder(R.drawable.ic_launcher_background) // Optional: A placeholder while loading
                    .into(ivProfile)

                btnAccept.setOnClickListener {
                    profile.interactionStatus = InteractionStatus.ACCEPTED
                    listener.onProfileClicked(profile)
                }

                btnReject.setOnClickListener {
                    profile.interactionStatus = InteractionStatus.DECLINED
                    listener.onProfileClicked(profile)
                }
            }
        }
    }

    inner class LoadingViewHolder(binding: LayoutLoadingItemBinding) : RecyclerView.ViewHolder(binding.root)
    // Nothing to bind, it's just a spinner

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdapterItem.ProfileItem -> ITEM_VIEW_TYPE_PROFILE
            is AdapterItem.LoadingItem -> ITEM_VIEW_TYPE_LOADING
            else -> throw IllegalStateException("Unknown view type at position $position")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_PROFILE -> {
                val binding = LayoutProfileCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProfileViewHolder(binding)
            }
            ITEM_VIEW_TYPE_LOADING -> {
                val binding = LayoutLoadingItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfileViewHolder -> {
                val profileItem = getItem(position) as AdapterItem.ProfileItem
                holder.bind(profileItem.userProfile)
            }
            is LoadingViewHolder -> {
                // No data to bind for the loading spinner
            }
        }
    }

    // 4. DiffUtil calculates the difference between two lists and enables smooth animations.
    class ProfileDiffCallback : DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return (oldItem is AdapterItem.ProfileItem && newItem is AdapterItem.ProfileItem && oldItem.userProfile.uid == newItem.userProfile.uid) ||
                    (oldItem is AdapterItem.LoadingItem && newItem is AdapterItem.LoadingItem)
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            // Check if the data content has changed. LoadingItem has no content to compare.
            return oldItem == newItem
        }
    }
}


sealed class AdapterItem {
    data class ProfileItem(val userProfile: UserProfile) : AdapterItem()
    object LoadingItem : AdapterItem()
}

interface ItemClickListener {
    fun onProfileClicked(profile: UserProfile)
}