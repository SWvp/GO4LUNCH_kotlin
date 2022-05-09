package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Predictions constructor(
    @SerializedName("predictions")
    @Expose
    var predictions: List<Prediction>? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null
)