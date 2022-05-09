package com.kardabel.go4lunch.presentation.ui.mapview

import com.google.android.gms.maps.model.LatLng

data class MapViewState constructor(
    val poiList: List<Poi>,
    val latLng: LatLng,
    val zoom: Float
)