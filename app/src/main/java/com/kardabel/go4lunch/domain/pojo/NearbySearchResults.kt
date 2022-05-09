package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NearbySearchResults constructor(
    // ADD EACH RESTAURANTS SEARCH IN A LIST
    @SerializedName("results")
    @Expose
    var results: List<Restaurant>? = null
)