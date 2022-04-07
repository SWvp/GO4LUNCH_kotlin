package com.kardabel.go4lunch.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo constructor(
    @SerializedName("width")
    @Expose
    var photoWidth: Int? = null,
    @SerializedName("height")
    @Expose
    var photoHeight: Int? = null,
    @SerializedName("html_attributions")
    @Expose
    var photoHtmlAttributions: List<String>? = null,
    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null

)