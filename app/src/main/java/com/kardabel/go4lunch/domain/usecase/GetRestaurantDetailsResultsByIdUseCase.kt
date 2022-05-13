package com.kardabel.go4lunch.domain.usecase

import androidx.lifecycle.LiveData
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult
import com.kardabel.go4lunch.domain.repository.RestaurantDetailsResponseRepository


class GetRestaurantDetailsResultsByIdUseCase(
    private val restaurantDetailsResponseRepository: RestaurantDetailsResponseRepository
) {

    fun invoke(placeId: String): LiveData<RestaurantDetailsResult?> = restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId)
}