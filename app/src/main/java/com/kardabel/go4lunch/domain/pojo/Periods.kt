package com.kardabel.go4lunch.domain.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Periods constructor(
    @SerializedName("close")
    @Expose
    var close: Close? = null,
    @SerializedName("open")
    @Expose
    var open: Open? = null
)

