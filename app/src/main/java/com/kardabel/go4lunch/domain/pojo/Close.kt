package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Close constructor(
    @SerializedName("day")
    @Expose
    var day: Int? = null,
    @SerializedName("time")
    @Expose
    var time: String? = null,
)
