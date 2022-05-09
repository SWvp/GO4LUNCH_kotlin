package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Geometry constructor(
    @SerializedName("location")
    @Expose
    var restaurantLatLngLiteral: RestaurantLatLngLiteral? = null
)