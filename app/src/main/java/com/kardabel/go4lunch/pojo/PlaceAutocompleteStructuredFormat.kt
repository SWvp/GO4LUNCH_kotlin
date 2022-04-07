package com.kardabel.go4lunch.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceAutocompleteStructuredFormat constructor(
    @SerializedName("main_text")
    @Expose
    var name: String? = null
)