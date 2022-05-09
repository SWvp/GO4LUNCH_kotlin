package com.kardabel.go4lunch.domain.model

data class UserWhoMadeRestaurantChoice constructor(
    var restaurantId: String? = null,
    var restaurantName: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var restaurantAddress: String? = null,

)