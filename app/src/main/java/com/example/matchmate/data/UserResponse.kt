package com.example.matchmate.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.room.*


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class UserResponse(
    @SerialName("results")
    val results: List<UserProfileResponse>
)

@Serializable
data class UserProfileResponse(

    @SerialName("email")
    val email: String,

    @SerialName("gender")
    val gender: String,

    @SerialName("name")
    val name: Name,

    @SerialName("location")
    val location: Location,

    @SerialName("id")
    val id: Id,

    @SerialName("phone")
    val phone: String,

    @SerialName("cell")
    val cell: String,

    @SerialName("picture")
    val picture: Picture
)


@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val uid: String, // e.g., the email or a unique ID from the API
    val gender: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val state: String,
    val phone: String,
    val cell: String,
    val pictureLarge: String,
    val pictureMedium: String,
    val pictureThumbnail: String,
    var interactionStatus: InteractionStatus = InteractionStatus.UNSEEN
)


@Entity(tableName = "seen_profiles")
data class SeenProfile(
    @PrimaryKey
    val uid: String // Stores only the unique ID of a fetched profile
)

@Serializable
data class Name(
    @SerialName("first")
    val first: String,

    @SerialName("last")
    val last: String
)

@Serializable
data class Location(
    @SerialName("city")
    val city: String,

    @SerialName("state")
    val state: String
)

@Serializable
data class Id(
    @SerialName("name")
    val name: String?,

    @SerialName("value")
    val value: String?
)

@Serializable
data class Picture(
    @SerialName("large")
    val large: String,

    @SerialName("medium")
    val medium: String,

    @SerialName("thumbnail")
    val thumbnail: String
)

enum class InteractionStatus {
    ACCEPTED,
    DECLINED,
    UNSEEN
}

fun UserProfileResponse.toEntity(): UserProfile {
    val uniqueId = this.id.value?.takeIf { it.isNotBlank() } ?: this.email
    return UserProfile(
        uid = uniqueId,
        gender = this.gender,
        firstName = this.name.first,
        lastName = this.name.last,
        city = this.location.city,
        state = this.location.state,
        phone = this.phone,
        cell = this.cell,
        pictureLarge = this.picture.large,
        pictureMedium = this.picture.medium,
        pictureThumbnail = this.picture.thumbnail,
        interactionStatus = InteractionStatus.UNSEEN
    )
}