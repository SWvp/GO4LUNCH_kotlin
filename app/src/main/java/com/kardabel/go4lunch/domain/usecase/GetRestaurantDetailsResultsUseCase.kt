package com.kardabel.go4lunch.domain.usecase

import android.app.Application
import android.location.Location
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult
import com.kardabel.go4lunch.domain.repository.LocationRepository
import com.kardabel.go4lunch.domain.repository.NearbySearchResponseRepository
import com.kardabel.go4lunch.domain.repository.RestaurantDetailsResponseRepository

class GetRestaurantDetailsResultsUseCase(
    locationRepository: LocationRepository,
    private val nearbySearchResponseRepository: NearbySearchResponseRepository,
    private val restaurantDetailsResponseRepository: RestaurantDetailsResponseRepository,
    private val application: Application,
) {

    companion object{
        const val RESTAURANT = "restaurant"
    }

    private val restaurantDetailsMediatorLiveData = MediatorLiveData<RestaurantDetailsResult>()
    private val restaurantDetailsList = mutableListOf<RestaurantDetailsResult>()

    // GET THE NEARBY SEARCH RESULT WITH USER LOCATION AS TRIGGER
    val nearbySearchResultsLiveData =
        Transformations.switchMap(locationRepository.getLocationLiveData()
        ) { input: Location ->
            val locationAsText =
                input.latitude.toString() + "," + input.longitude
            nearbySearchResponseRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius))
        }


    // THE COMBINE METHOD ALLOW NULL ARGS, SO LETS NEARBY TRIGGER THE COMBINE,
    // THEN, WHEN DETAILS RESULT IS SEND BY REPO, TRIGGER COMBINE TO SET LIVEDATA VALUE
    val invoke =
        MediatorLiveData<List<RestaurantDetailsResult>>().apply {
            addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
                combine(
                    nearbySearchResults,
                    null
                )
            }
            addSource(restaurantDetailsMediatorLiveData) { restaurantDetailsResult ->
                combine(
                    nearbySearchResultsLiveData.value,
                    restaurantDetailsResult
                )
            }
        }

    private fun combine(nearbySearchResults: NearbySearchResults?, restaurantDetailsResult: RestaurantDetailsResult?) {
        nearbySearchResults?.results?.let { restaurantList ->

            for(restaurant in restaurantList){
                if (!restaurantDetailsList.contains(restaurantDetailsResult) || restaurantDetailsResult == null){
                    val placeId = restaurant.restaurantId
                    restaurantDetailsMediatorLiveData.addSource(restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId!!)) { restaurantDetailsResult ->
                        restaurantDetailsList.add(restaurantDetailsResult!!)
                        restaurantDetailsMediatorLiveData.setValue(restaurantDetailsResult)

                    }
                }
            }
            invoke.setValue(restaurantDetailsList)
        }
    }
}