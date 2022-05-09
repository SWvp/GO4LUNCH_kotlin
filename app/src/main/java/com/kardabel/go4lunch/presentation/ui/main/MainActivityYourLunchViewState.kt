package com.kardabel.go4lunch.presentation.ui.main

data class MainActivityYourLunchViewState constructor(
    val restaurantId: String? = null,
    val currentUserRestaurantChoiceStatus: Int = 0
)