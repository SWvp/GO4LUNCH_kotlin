package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FavoriteRestaurant constructor(
    @SerializedName("place_id")
    @Expose
    var restaurantId: String? = null
)