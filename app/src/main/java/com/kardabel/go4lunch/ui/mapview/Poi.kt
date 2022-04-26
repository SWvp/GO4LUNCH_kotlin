package com.kardabel.go4lunch.ui.mapview

import com.google.android.gms.maps.model.LatLng

data class Poi constructor(
    val poiName: String? = null,
    val poiPlaceId: String? = null,
    val poiAddress: String? = null,
    val poiLatLng: LatLng? = null,
    val isFavorite: Boolean = false
)