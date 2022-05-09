package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.SerializedName

data class RestaurantDetailsResult constructor(
    @SerializedName("result")
    var result: RestaurantDetails? = null

)