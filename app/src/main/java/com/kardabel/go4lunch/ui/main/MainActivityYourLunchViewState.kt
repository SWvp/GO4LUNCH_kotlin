package com.kardabel.go4lunch.ui.main

data class MainActivityYourLunchViewState constructor(
    val restaurantId: String? = null,
    val currentUserRestaurantChoiceStatus: Int = 0
)