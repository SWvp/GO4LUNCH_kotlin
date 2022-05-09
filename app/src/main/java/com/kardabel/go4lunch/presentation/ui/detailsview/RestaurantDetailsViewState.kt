package com.kardabel.go4lunch.presentation.ui.detailsview

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class RestaurantDetailsViewState constructor(
    val detailsRestaurantName: String? = null,
    val detailsRestaurantAddress: String? = null,
    val detailsPhoto: String? = null,
    val detailsRestaurantNumber: String? = null,
    val detailsWebsite: String? = null,
    val detailsRestaurantId: String? = null,
    val rating: Double = 0.0,
    @DrawableRes
    val choseRestaurantButton: Int = 0,
    @DrawableRes
    val detailLikeButton: Int = 0,
    @ColorRes
    val backgroundColor: Int = 0,
)