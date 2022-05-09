package com.kardabel.go4lunch.presentation.ui.detailsview

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.kardabel.go4lunch.BuildConfig
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.domain.model.UserModel
import com.kardabel.go4lunch.domain.model.UserWhoMadeRestaurantChoice
import com.kardabel.go4lunch.domain.pojo.FavoriteRestaurant
import com.kardabel.go4lunch.domain.pojo.Photo
import com.kardabel.go4lunch.domain.pojo.Restaurant
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult
import com.kardabel.go4lunch.domain.repository.FavoriteRestaurantsRepository
import com.kardabel.go4lunch.domain.repository.UsersWhoMadeRestaurantChoiceRepository
import com.kardabel.go4lunch.domain.repository.WorkmatesRepository
import com.kardabel.go4lunch.domain.usecase.*
import kotlin.math.roundToInt

class RestaurantDetailsViewModel constructor(
    private val application: Application,
    private val getNearbySearchResultsByIdUseCase: GetNearbySearchResultsByIdUseCase,
    private val getRestaurantDetailsResultsByIdUseCase: GetRestaurantDetailsResultsByIdUseCase,
    private val usersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository,
    private val workmatesRepository: WorkmatesRepository,
    private val favoriteRestaurantsRepository: FavoriteRestaurantsRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val clickOnChoseRestaurantButtonUseCase: ClickOnChoseRestaurantButtonUseCase,
    private val clickOnFavoriteRestaurantUseCase: ClickOnFavoriteRestaurantUseCase,
) : ViewModel() {

    val restaurantDetailsViewStateMediatorLiveData =
        MediatorLiveData<RestaurantDetailsViewState>()
    val workmatesLikeThisRestaurantMediatorLiveData =
        MediatorLiveData<List<RestaurantDetailsWorkmatesViewState>>()

    // INIT THE VIEW MODEL WITH THE PLACEID SEND IN THE INTENT
    fun init(placeId: String) {
        val restaurantLiveData: LiveData<Restaurant> =
            getNearbySearchResultsByIdUseCase.invoke(placeId)
        val restaurantDetailsLiveData: LiveData<RestaurantDetailsResult> =
            getRestaurantDetailsResultsByIdUseCase.invoke(placeId)
        val workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<UserWhoMadeRestaurantChoice>> =
            usersWhoMadeRestaurantChoiceRepository.workmatesWhoMadeRestaurantChoice
        val favoriteRestaurantsLiveData: LiveData<List<FavoriteRestaurant>> =
            favoriteRestaurantsRepository.favoriteRestaurants
        val workMatesLiveData: LiveData<List<UserModel>> = workmatesRepository.workmates


        // OBSERVERS FOR RESTAURANT DETAILS
        restaurantDetailsViewStateMediatorLiveData.addSource(restaurantLiveData) { restaurant ->
            combine(
                restaurant,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsLiveData.value,
                favoriteRestaurantsLiveData.value)
        }

        restaurantDetailsViewStateMediatorLiveData.addSource(restaurantDetailsLiveData) { restaurantDetailsResults ->
            combine(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsResults,
                favoriteRestaurantsLiveData.value)
        }

        restaurantDetailsViewStateMediatorLiveData.addSource(
            workmatesWhoMadeRestaurantChoiceLiveData) { workmatesWithFavoriteRestaurant ->
            combine(
                restaurantLiveData.value,
                workmatesWithFavoriteRestaurant,
                restaurantDetailsLiveData.value,
                favoriteRestaurantsLiveData.value)
        }


        restaurantDetailsViewStateMediatorLiveData.addSource(favoriteRestaurantsLiveData) { favoriteRestaurants ->
            combine(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                restaurantDetailsLiveData.value,
                favoriteRestaurants)
        }


        // OBSERVERS FOR WORKMATES RECYCLERVIEW
        workmatesLikeThisRestaurantMediatorLiveData.addSource(restaurantLiveData) { restaurantSearch ->
            combineWorkmates(
                restaurantSearch,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                workMatesLiveData.value)
        }
        workmatesLikeThisRestaurantMediatorLiveData.addSource(
            workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
            combineWorkmates(
                restaurantLiveData.value,
                userWithFavoriteRestaurants,
                workMatesLiveData.value)
        }

        workmatesLikeThisRestaurantMediatorLiveData.addSource(workMatesLiveData) { userModels ->
            combineWorkmates(
                restaurantLiveData.value,
                workmatesWhoMadeRestaurantChoiceLiveData.value,
                userModels)
        }


    }

    private fun combine(
        restaurant: Restaurant?,
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
        restaurantDetails: RestaurantDetailsResult?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ) {

        if (restaurant != null && restaurantDetails == null) {
            restaurantDetailsViewStateMediatorLiveData.setValue(mapWithoutDetails(
                restaurant,
                userWhoMadeRestaurantChoices,
                favoriteRestaurants))
        } else if (restaurant != null && favoriteRestaurants != null) {
            restaurantDetailsViewStateMediatorLiveData.value = map(
                restaurant,
                userWhoMadeRestaurantChoices,
                restaurantDetails,
                favoriteRestaurants)
        }
    }

    private fun combineWorkmates(
        restaurant: Restaurant?,
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
        users: List<UserModel>?,
    ) {

        if (userWhoMadeRestaurantChoices != null && users != null && restaurant != null) {
            workmatesLikeThisRestaurantMediatorLiveData.value = mapWorkmates(
                restaurant,
                userWhoMadeRestaurantChoices,
                users)
        }
    }

    @SuppressLint("ResourceType")
    private fun mapWithoutDetails(
        restaurant: Restaurant,
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ): RestaurantDetailsViewState {

        val userId: String = getCurrentUserIdUseCase.invoke()

        var restaurantChoiceState: Int = R.drawable.has_not_decided
        var backgroundVectorColor =
            Color.parseColor(application.getString(R.string.background_black))

        // CHECK IF USER HAVE A RESTAURANT CHOICE AND SET THE CHOICE ICON PROPERLY
        if (isUserGetRestaurantChoice(userWhoMadeRestaurantChoices, restaurant, userId)) {
            restaurantChoiceState = R.drawable.has_decided
            backgroundVectorColor =
                Color.parseColor(application.getString(R.string.background_green))
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        var detailLikeButton = R.drawable.detail_favorite_star_empty
        favoriteRestaurants?.let { favoriteRestaurantList ->
            for (restaurantInFavorite in favoriteRestaurantList) {
                if (restaurantInFavorite.restaurantId!! == restaurant.restaurantId)
                    detailLikeButton = R.drawable.details_favorite_star_full
            }
        }

        return RestaurantDetailsViewState(
            restaurant.restaurantName,
            restaurant.restaurantAddress,
            application.getString(R.string.api_url)
                    + application.getString(R.string.photo_reference)
                    + photoReference(restaurant.restaurantPhotos)
                    + application.getString(R.string.and_key)
                    + BuildConfig.GOOGLE_PLACES_KEY,
            application.getString(R.string.phone_number_unavailable),
            application.getString(R.string.website_unavailable),
            restaurant.restaurantId,
            convertRatingStars(restaurant.rating),
            restaurantChoiceState,
            detailLikeButton,
            backgroundVectorColor
        )
    }

    @SuppressLint("ResourceType")
    private fun map(
        restaurant: Restaurant,
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
        restaurantDetails: RestaurantDetailsResult?,
        favoriteRestaurants: List<FavoriteRestaurant>?,
    ): RestaurantDetailsViewState {

        val userId = getCurrentUserIdUseCase.invoke()
        var restaurantChoiceState: Int = R.drawable.has_not_decided
        var backgroundVectorColor =
            Color.parseColor(application.getString(R.string.background_black))
        var restaurantPhoneNumber: String
        var restaurantWebsite: String

        // CHECK IF USER HAVE A RESTAURANT CHOICE AND SET THE CHOICE ICON PROPERLY
        if (isUserGetRestaurantChoice(userWhoMadeRestaurantChoices, restaurant, userId)) {
            restaurantChoiceState = R.drawable.has_decided
            backgroundVectorColor =
                Color.parseColor(application.getString(R.string.background_green))
        }

        // CHECK IF PHONE NUMBER IS AVAILABLE
        restaurantPhoneNumber = application.getString(R.string.phone_number_unavailable)
        restaurantDetails?.result?.formattedPhoneNumber?.let {
            restaurantPhoneNumber = restaurantDetails.result!!.formattedPhoneNumber!!
        }

        // CHECK IF WEBSITE ADDRESS IS AVAILABLE
        restaurantWebsite = application.getString(R.string.website_unavailable)
        restaurantDetails?.result?.website?.let {
            restaurantWebsite = restaurantDetails.result!!.website!!
        }

        // CHECK IF THIS RESTAURANT IS IN USERS FAVORITE
        var detailLikeButton = R.drawable.detail_favorite_star_empty
        favoriteRestaurants?.let { favoriteRestaurantList ->
            for (restaurantInFavorite in favoriteRestaurantList) {
                if (restaurantInFavorite.restaurantId!! == restaurant.restaurantId)
                    detailLikeButton = R.drawable.details_favorite_star_full
            }
        }

        return RestaurantDetailsViewState(
            restaurant.restaurantName,
            restaurant.restaurantAddress,
            application.getString(R.string.api_url)
                    + application.getString(R.string.photo_reference)
                    + photoReference(restaurant.restaurantPhotos)
                    + application.getString(R.string.and_key)
                    + BuildConfig.GOOGLE_PLACES_KEY,
            restaurantPhoneNumber,
            restaurantWebsite,
            restaurant.restaurantId,
            convertRatingStars(restaurant.rating),
            restaurantChoiceState,
            detailLikeButton,
            backgroundVectorColor
        )
    }

    private fun mapWorkmates(
        restaurant: Restaurant,
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>,
        users: List<UserModel>,
    ): List<RestaurantDetailsWorkmatesViewState> {

        val workMatesViewStateList = mutableListOf<RestaurantDetailsWorkmatesViewState>()

        for (userWhoMadeChoice in userWhoMadeRestaurantChoices) {
            if (userWhoMadeChoice.restaurantId == restaurant.restaurantId) {
                for (user in users) {
                    if (user.uid == userWhoMadeChoice.userId) {
                        val name = user.userName + " " + application.getString(R.string.is_joining)
                        val avatar = user.avatarURL
                        workMatesViewStateList.add(RestaurantDetailsWorkmatesViewState(
                            name,
                            avatar!!
                        ))
                    }
                }
            }
        }
        return workMatesViewStateList
    }

    private fun isUserGetRestaurantChoice(
        userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
        restaurant: Restaurant,
        userId: String?,
    ): Boolean {

        userWhoMadeRestaurantChoices?.let {
            for (user in userWhoMadeRestaurantChoices) {
                if (user.restaurantId == restaurant.restaurantId && user.userId == userId) {
                    return true
                }
            }
        }
        return false
    }

    private fun photoReference(restaurantPhotos: List<Photo>?): String {

        restaurantPhotos?.let { photoList ->
            for (photo in photoList)
                if (photo.photoReference!!.isNotEmpty()) {
                    return photo.photoReference!!
                }
        }
        return application.getString(R.string.photo_unavailable)
    }

    private fun convertRatingStars(rating: Double): Double {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return (rating * 3 / 5).roundToInt().toDouble()
    }

    // CLICK ON THE CHOSE FAB
    fun onChoseRestaurantButtonClick(
        restaurantId: String,
        restaurantName: String,
        restaurantAddress: String,
    ) {
        clickOnChoseRestaurantButtonUseCase.onRestaurantSelectedClick(
            restaurantId,
            restaurantName,
            restaurantAddress)
    }

    // CLICK ON THE FAVORITE ICON
    fun onFavoriteIconClick(
        restaurantId: String,
        restaurantName: String,
    ) {
        clickOnFavoriteRestaurantUseCase.onFavoriteRestaurantClick(restaurantId, restaurantName)
    }
}