package com.kardabel.go4lunch.domain.usecase

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults
import com.kardabel.go4lunch.domain.repository.LocationRepository
import com.kardabel.go4lunch.domain.repository.NearbySearchResponseRepository

class GetNearbySearchResultsUseCase(
    locationRepository: LocationRepository,
    private val nearbySearchResponseRepository: NearbySearchResponseRepository,
    private val application: Application,

    ) {

    companion object{
        const val RESTAURANT = "restaurant"
    }

    var invoke: LiveData<NearbySearchResults> =
        Transformations.switchMap(locationRepository.getLocationLiveData()) { input: Location ->
            val locationAsText = input.latitude.toString() + "," + input.longitude
            nearbySearchResponseRepository.getRestaurantListLiveData(
                RESTAURANT,
                locationAsText,
                application.getString(R.string.radius))
        }

}