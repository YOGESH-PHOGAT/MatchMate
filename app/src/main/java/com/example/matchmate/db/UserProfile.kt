package com.example.matchmate.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.matchmate.data.InteractionStatus

sealed interface UserProfile {
    val uid: String
    val gender: String
    val firstName: String
    val lastName: String
    val city: String
    val state: String
    val phone: String
    val cell: String
    val pictureLarge: String
    val pictureMedium: String
    val pictureThumbnail: String
    var interactionStatus: InteractionStatus
}

@Entity(tableName = "user_profiles")
data class CardProfile(
    @PrimaryKey
    override val uid: String,
    override val gender: String,
    override val firstName: String,
    override val lastName: String,
    override val city: String,
    override val state: String,
    override val phone: String,
    override val cell: String,
    override val pictureLarge: String,
    override val pictureMedium: String,
    override val pictureThumbnail: String,
    override var interactionStatus: InteractionStatus = InteractionStatus.UNSEEN
) : UserProfile

@Entity(tableName = "history_profiles")
data class HistoryProfile(
    @PrimaryKey
    override val uid: String,
    override val gender: String,
    override val firstName: String,
    override val lastName: String,
    override val city: String,
    override val state: String,
    override val phone: String,
    override val cell: String,
    override val pictureLarge: String,
    override val pictureMedium: String,
    override val pictureThumbnail: String,
    override var interactionStatus: InteractionStatus
) : UserProfile
