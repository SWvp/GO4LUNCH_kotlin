package com.kardabel.go4lunch.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Restaurant constructor(
    @SerializedName("place_id")
    @Expose
    var restaurantId: String? = null,
    @SerializedName("name")
    @Expose
    var restaurantName: String? = null,
    @SerializedName("vicinity")
    @Expose
    var restaurantAddress: String? = null,
    @SerializedName("photos")
    @Expose
    var restaurantPhotos: List<Photo>? = null,
    @SerializedName("geometry")
    @Expose
    var restaurantGeometry: Geometry? = null,
    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null,
    @SerializedName("rating")
    @Expose
    var rating: Double = 0.0,
    @SerializedName("user_ratings_total")
    @Expose
    var totalRatings: Int = 0,
    @SerializedName("permanently_closed")
    @Expose
    var isPermanentlyClosed: Boolean = false,
    @SerializedName("formatted_phone_number")
    @Expose
    var formattedPhoneNumber: String? = null,
    @SerializedName("website")
    @Expose
    var website: String? = null

)