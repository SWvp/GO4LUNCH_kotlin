package com.kardabel.go4lunch.ui.mapview

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice
import com.kardabel.go4lunch.pojo.NearbySearchResults
import com.kardabel.go4lunch.repository.LocationRepository
import com.kardabel.go4lunch.repository.UserSearchRepository
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase

class MapViewModel(
    locationRepository: LocationRepository,
    getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase,
    usersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository,
    userSearchRepository: UserSearchRepository
) : ViewModel() {

    companion object {
        private const val ZOOM_FOCUS = 15f
    }

    private var locationLiveData: LiveData<Location> = locationRepository.locationLiveData
    private var nearbySearchResultsLiveData: LiveData<NearbySearchResults> = getNearbySearchResultsUseCase.invoke()
    private var workmatesWhoMadeRestaurantChoiceLiveData: LiveData<MutableList<UserWhoMadeRestaurantChoice>> = usersWhoMadeRestaurantChoiceRepository.workmatesWhoMadeRestaurantChoice
    private var usersSearchLiveData: LiveData<String> = userSearchRepository.usersSearchLiveData

    private val mapViewStatePoiMediatorLiveData = MediatorLiveData<MapViewState>().apply {
        addSource(locationLiveData) { location ->
            combine(
                location,
                nearbySearchResultsLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearchLiveData.value
            )
        }
        addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
            combine(
                locationLiveData.value,
                nearbySearchResults,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearchLiveData.value
            )
        }
        addSource(workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
            combine(
                locationLiveData.value,
                nearbySearchResultsLiveData.value,
                userWithFavoriteRestaurants,
                usersSearchLiveData.value
            )
        }
        addSource(usersSearchLiveData) { usersSearch ->
            combine(
                locationLiveData.value,
                nearbySearchResultsLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                usersSearch
            )
        }
    }

    private fun combine(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>?,
        usersSearch: String?
    ) {
        if (usersSearch != null && usersSearch.isNotEmpty()) {
            mapViewStatePoiMediatorLiveData.value = mapUsersSearch(
                location,
                nearbySearchResults,
                userWhoMadeRestaurantChoice,
                usersSearch
            )
        } else if (nearbySearchResults != null && location != null) {
            mapViewStatePoiMediatorLiveData.value = map(
                location,
                nearbySearchResults,
                userWhoMadeRestaurantChoice
            )
        }
    }

    // MAP WITH USER'S SEARCH ONLY
    private fun mapUsersSearch(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>?,
        usersSearch: String
    ): MapViewState {

        val poiList = mutableListOf<Poi>()
        val restaurantAsFavoriteId = mutableListOf<String>()

        // Add id from restaurant who as been set as favorite
        if (userWhoMadeRestaurantChoice != null) {
            for (i in userWhoMadeRestaurantChoice.indices) {
                restaurantAsFavoriteId.add(userWhoMadeRestaurantChoice[i].restaurantId!!)
            }
        }

        for (i in nearbySearchResults!!.results!!.indices) {
            if (nearbySearchResults.results!![i].restaurantName!!.contains(usersSearch)) {
                poiList.add(poi(nearbySearchResults, i, userWhoMadeRestaurantChoice, restaurantAsFavoriteId))
            }
        }
        val userLocation = LatLng(
            location!!.latitude,
            location.longitude
        )

        return MapViewState(
            poiList,
            LatLng(
                userLocation.latitude,
                userLocation.longitude
            ),
            Companion.ZOOM_FOCUS
        )
    }

    private fun map(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>?,
    ): MapViewState {

        val poiList = mutableListOf<Poi>()
        val restaurantAsFavoriteId = mutableListOf<String>()

        // Add id from restaurant who as been set as favorite
        if (userWhoMadeRestaurantChoice != null) {
            for (i in userWhoMadeRestaurantChoice.indices) {
                restaurantAsFavoriteId.add(userWhoMadeRestaurantChoice[i].restaurantId!!)
            }
        }

        for (i in nearbySearchResults!!.results!!.indices) {
            poiList.add(poi(nearbySearchResults, i, userWhoMadeRestaurantChoice, restaurantAsFavoriteId))
        }

        val userLocation = LatLng(
            location!!.latitude,
            location.longitude
        )

        return MapViewState(
            poiList,
            LatLng(
                userLocation.latitude,
                userLocation.longitude
            ),
            ZOOM_FOCUS
        )
    }

    private fun poi(
        nearbySearchResults: NearbySearchResults,
        i: Int,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>?,
        restaurantAsFavoriteId : List<String>
    ): Poi {
        var isFavorite = false
        val poiName = nearbySearchResults.results!![i].restaurantName
        val poiPlaceId = nearbySearchResults.results!![i].restaurantId
        val poiAddress = nearbySearchResults.results!![i].restaurantAddress
        val latLng = LatLng(
            nearbySearchResults
                .results!![i]
                .restaurantGeometry?.restaurantLatLngLiteral?.lat!!,
            nearbySearchResults
                .results!![i]
                .restaurantGeometry?.restaurantLatLngLiteral?.lng!!
        )

        if (userWhoMadeRestaurantChoice != null
            && restaurantAsFavoriteId.contains(poiPlaceId)
        ) {
            isFavorite = true
        }

        return  Poi(
            poiName,
            poiPlaceId,
            poiAddress,
            latLng,
            isFavorite
        )
    }

    // LIVEDATA OBSERVED BY MAP FRAGMENT
    fun getMapViewStateLiveData(): LiveData<MapViewState?> {
        return mapViewStatePoiMediatorLiveData
    }
}