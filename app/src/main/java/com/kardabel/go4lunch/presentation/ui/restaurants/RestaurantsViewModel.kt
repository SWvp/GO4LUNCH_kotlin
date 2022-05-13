package com.kardabel.go4lunch.presentation.ui.restaurants

import android.app.Application
import android.graphics.Color
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.kardabel.go4lunch.BuildConfig
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.domain.model.UserWhoMadeRestaurantChoice
import com.kardabel.go4lunch.domain.pojo.*
import com.kardabel.go4lunch.domain.repository.LocationRepository
import com.kardabel.go4lunch.domain.repository.UserSearchRepository
import com.kardabel.go4lunch.domain.repository.UsersWhoMadeRestaurantChoiceRepository
import com.kardabel.go4lunch.domain.usecase.GetNearbySearchResultsUseCase
import com.kardabel.go4lunch.domain.usecase.GetRestaurantDetailsResultsUseCase
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt

class RestaurantsViewModel constructor(
    private val application: Application,
    locationRepository: LocationRepository,
    getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase,
    getRestaurantDetailsResultsUseCase: GetRestaurantDetailsResultsUseCase,
    usersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository,
    userSearchRepository: UserSearchRepository,
    private val clock: Clock
) : ViewModel() {

    private var locationLiveData: LiveData<Location> = locationRepository.getLocationLiveData()
    private var nearbySearchResultsLiveData: LiveData<NearbySearchResults> =
        getNearbySearchResultsUseCase.invoke
    private var restaurantsDetailsResultLiveData: LiveData<List<RestaurantDetailsResult>> =
        getRestaurantDetailsResultsUseCase.invoke
    private var workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<UserWhoMadeRestaurantChoice>> =
        usersWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice()
    private var usersSearchLiveData: LiveData<String> = userSearchRepository.getUsersSearchLiveData()

    val getRestaurantsWrapperViewStateMediatorLiveData =
        MediatorLiveData<RestaurantsWrapperViewState>().apply {
            addSource(locationLiveData) { location ->
                combine(
                    location,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(nearbySearchResultsLiveData) { nearbySearchResults ->
                combine(
                    locationLiveData.value,
                    nearbySearchResults,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(restaurantsDetailsResultLiveData) { restaurantDetailsResults ->
                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantDetailsResults,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearchLiveData.value
                )
            }
            addSource(workmatesWhoMadeRestaurantChoiceLiveData) { userWithFavoriteRestaurants ->
                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    userWithFavoriteRestaurants,
                    usersSearchLiveData.value
                )
            }
            addSource(usersSearchLiveData) { usersSearch ->
                combine(
                    locationLiveData.value,
                    nearbySearchResultsLiveData.value,
                    restaurantsDetailsResultLiveData.value,
                    workmatesWhoMadeRestaurantChoiceLiveData.value,
                    usersSearch
                )
            }
        }

    private fun combine(
        location: Location?,
        nearbySearchResults: NearbySearchResults?,
        restaurantDetailsResults: List<RestaurantDetailsResult>?,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>?,
        usersSearch: String?
    ) {

        if (location != null && userWhoMadeRestaurantChoice != null) {
            if (nearbySearchResults != null && restaurantDetailsResults != null && usersSearch != null && usersSearch.isNotEmpty()) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapUsersSearch(
                    nearbySearchResults,
                    restaurantDetailsResults,
                    location,
                    userWhoMadeRestaurantChoice,
                    usersSearch
                )
            } else if (restaurantDetailsResults == null && nearbySearchResults != null) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapWithoutDetails(
                    location,
                    nearbySearchResults,
                    userWhoMadeRestaurantChoice
                )
            } else if (restaurantDetailsResults != null && nearbySearchResults != null) {
                getRestaurantsWrapperViewStateMediatorLiveData.value = mapWithDetails(
                    location,
                    nearbySearchResults,
                    restaurantDetailsResults,
                    userWhoMadeRestaurantChoice
                )
            }
        }
    }

    //************************************************************************//
    ///////////////////////////     DATA MAPPING     ///////////////////////////
    //************************************************************************//

    // 1.USER SEARCH RESULT, EXPOSE ONLY ONE RESULT
    private fun mapUsersSearch(
        nearbySearchResults: NearbySearchResults,
        restaurantDetailsResults: List<RestaurantDetailsResult>,
        location: Location,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>,
        usersSearch: String
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (i in 0 until nearbySearchResults.results!!.size) {

            if (nearbySearchResults.results!![i].restaurantName!!.contains(usersSearch)) {

                val distanceInt = distance(location, nearbySearchResults.results!![i])
                val name = nearbySearchResults.results!![i].restaurantName
                val address = nearbySearchResults.results!![i].restaurantAddress
                val photo = nearbySearchResults.results!![i].restaurantPhotos?.let { photoList ->
                    photoReference(
                        photoList
                    )
                }
                val distance: String = distanceInt.toString() + application.getString(R.string.m)
                val openingHours = getOpeningHours(
                    restaurantDetailsResults[i].result?.openingHours,
                    nearbySearchResults.results!![i].isPermanentlyClosed
                )
                val rating: Double = convertRatingStars(nearbySearchResults.results!![i].rating)
                val restaurantId = nearbySearchResults.results!![i].restaurantId
                val usersWhoChoseThisRestaurant: String =
                    usersWhoChoseThisRestaurant(restaurantId, userWhoMadeRestaurantChoice)
                val textColor: Int = getTextColor(openingHours)

                restaurantList.add(
                    RestaurantsViewState(
                        distanceInt,
                        name,
                        address,
                        photo,
                        distance,
                        openingHours,
                        rating,
                        restaurantId,
                        usersWhoChoseThisRestaurant,
                        textColor
                    )
                )
            }
        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    // 2.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE CAUSE TO CONNECTION PROBLEM)
    private fun mapWithoutDetails(
        location: Location,
        nearbySearchResults: NearbySearchResults,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (i in 1 until nearbySearchResults.results!!.size) {

            val distanceInt = distance(location, nearbySearchResults.results!![i])
            val name = nearbySearchResults.results!![i].restaurantName
            val address = nearbySearchResults.results!![i].restaurantAddress
            val photo = nearbySearchResults.results!![i].restaurantPhotos?.let { photoList ->
                photoReference(
                    photoList
                )
            }
            val distance: String = distanceInt.toString() + application.getString(R.string.m)
            val openingHours =
                getOpeningHoursWithoutDetails(nearbySearchResults.results!![i].openingHours)
            val rating: Double = convertRatingStars(nearbySearchResults.results!![i].rating)
            val restaurantId = nearbySearchResults.results!![i].restaurantId
            val usersWhoChoseThisRestaurant: String =
                usersWhoChoseThisRestaurant(restaurantId, userWhoMadeRestaurantChoice)
            val textColor: Int = getTextColor(openingHours)

            restaurantList.add(
                RestaurantsViewState(
                    distanceInt,
                    name,
                    address,
                    photo,
                    distance,
                    openingHours,
                    rating,
                    restaurantId,
                    usersWhoChoseThisRestaurant,
                    textColor
                )
            )
        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    // 3.NEARBY SEARCH WITH PLACE DETAILS DATA
    private fun mapWithDetails(
        location: Location,
        nearbySearchResults: NearbySearchResults,
        restaurantDetailsResults: List<RestaurantDetailsResult>,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>
    ): RestaurantsWrapperViewState {

        val restaurantList = mutableListOf<RestaurantsViewState>()

        for (restaurant in nearbySearchResults.results!!) {
            for (i in restaurantDetailsResults.indices) {
                if (restaurantDetailsResults[i].result!!.placeId.equals(restaurant.restaurantId)) {

                    val distanceInt = distance(location, restaurant)
                    val name = restaurant.restaurantName
                    val address = restaurant.restaurantAddress
                    val photo = restaurant.restaurantPhotos?.let { photoList ->
                        photoReference(
                            photoList
                        )
                    }
                    val distance: String = distanceInt.toString() + application.getString(R.string.m)
                    val openingHours =
                        getOpeningHours(restaurantDetailsResults[i].result!!.openingHours, restaurant.isPermanentlyClosed)
                    val rating: Double = convertRatingStars(restaurant.rating)
                    val restaurantId = restaurant.restaurantId
                    val usersWhoChoseThisRestaurant: String =
                        usersWhoChoseThisRestaurant(restaurantId, userWhoMadeRestaurantChoice)
                    val textColor: Int = getTextColor(openingHours)

                    restaurantList.add(
                        RestaurantsViewState(
                            distanceInt,
                            name,
                            address,
                            photo,
                            distance,
                            openingHours,
                            rating,
                            restaurantId,
                            usersWhoChoseThisRestaurant,
                            textColor
                        )
                    )
                }
            }

        }
        // COMPARATOR TO SORT LIST BY DISTANCE FROM THE USER LOCATION
        Collections.sort(restaurantList, Comparator.comparingInt(RestaurantsViewState::distanceInt))

        return RestaurantsWrapperViewState(restaurantList)
    }


    private fun distance(location: Location, restaurant: Restaurant): Int {

        val results = FloatArray(1)
        val restaurantLat = restaurant.restaurantGeometry!!.restaurantLatLngLiteral!!.lat
        val restaurantLng = restaurant.restaurantGeometry!!.restaurantLatLngLiteral!!.lng

        when {
            restaurantLat != null && restaurantLng != null -> {
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    restaurantLat,
                    restaurantLng,
                    results
                )
            }
        }
        return results[0].toInt()

    }

    private fun photoReference(photoList: List<Photo>?): String {

        var result: String? = null

        if (photoList != null) {
            for (photo in photoList) when {
                photo.photoReference?.isNotEmpty() == true -> {
                    result = (application.getString(R.string.api_url)
                            + application.getString(R.string.photo_reference)
                            + photo.photoReference
                            + application.getString(R.string.and_key)
                            + BuildConfig.GOOGLE_PLACES_KEY)
                }
            }
        }
        return result!!
    }

    private fun getOpeningHours(
        openingHours: OpeningHours?,
        permanentlyClosedStatus: Boolean
    ): String {

        // DEFAULT MESSAGE IF NO OPENING HOUR DATA
        var messageToDisplay = application.getString(R.string.opening_hours_unavailable)

        if (openingHours != null) {
            val currentLocalDate = LocalDateTime.now(clock)
            // IF THE PERIOD LIST HAS ONLY ONE ELEMENT, CONSIDER THAT THE PLACE IS A 24/7 ONE
            if (openingHours.periods != null) {
                if (openingHours.periods!!.size == 1) {
                    if (openingHours.openNow!!) {
                        messageToDisplay = application.getString(R.string.open_h24)
                    }
                } else if (openingHours.periods!!.size > 1) {
                    var selectedOpeningDateTime: LocalDateTime? = null
                    var selectedClosingDateTime: LocalDateTime? = null

                    // SET OPENING AND CLOSED HOURS IF AFTER CURRENT DATE, TAKE THE CLOSEST MATCH
                    for (period in openingHours.periods!!) {

                        val openingHourToConsider: LocalDateTime = convertOpeningHours(
                            period.open!!.time!!,
                            period.open!!.day!!
                        )
                        val closingHourToConsider: LocalDateTime = convertOpeningHours(
                            period.close!!.time!!,
                            period.close!!.day!!
                        )
                        if (openingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(
                                selectedOpeningDateTime,
                                openingHourToConsider
                            )
                        ) {
                            selectedOpeningDateTime = openingHourToConsider
                        }
                        if (closingHourToConsider.isAfter(currentLocalDate) && isConsiderClosestThanSelected(
                                selectedClosingDateTime,
                                closingHourToConsider
                            )
                        ) {
                            selectedClosingDateTime = closingHourToConsider
                        }
                    }
                    if (selectedOpeningDateTime != null) {
                        if (selectedOpeningDateTime.isAfter(selectedClosingDateTime)) {
                            val closingSoonDate =
                                selectedClosingDateTime!!.minus(1, ChronoUnit.HOURS)
                            messageToDisplay =
                                application.getString(R.string.open_until) + getReadableHour(
                                    selectedClosingDateTime
                                )

                            if (currentLocalDate.isAfter(closingSoonDate)) {
                                messageToDisplay = application.getString(R.string.closing_soon)
                            }
                        } else if (selectedOpeningDateTime.isAfter(currentLocalDate)) {
                            messageToDisplay = application.getString(R.string.closed_until) +
                                    getReadableDay(selectedOpeningDateTime) +
                                    getReadableHour(selectedOpeningDateTime)
                        }
                    }
                }// IF THE PERIOD LIST IS EMPTY, RETRIEVE ONLY OPEN STATUS
            } else if (!openingHours.openNow!!) {
                messageToDisplay = application.getString(R.string.closed)
            } else if (openingHours.openNow!!) {
                messageToDisplay = application.getString(R.string.open)
            }
        }
        if (permanentlyClosedStatus) {
            messageToDisplay = application.getString(R.string.permanently_closed)
        }
        return messageToDisplay
    }

    private fun convertOpeningHours(time: String, day: Int): LocalDateTime {

        val hour = time.substring(0, 2)
        val minutes = time.substring(2, 4)
        val hourInt = hour.toInt()
        val minuteInt = minutes.toInt()
        var dayToAdd: Int = day - getCurrentNumericDay()

        when {
            dayToAdd < 0 -> {
                dayToAdd += 7
            }
        }

        var dayOfMonth: Int = getCurrentDayOfMonth() + dayToAdd
        var month: Int = currentMonth()
        var year: Int = currentYear()

        when {
            dayOfMonth > 30 && isEvenMonth() -> {
                dayOfMonth -= 30
                month += 1
            }
        }
        when {
            dayOfMonth > 31 -> {
                dayOfMonth -= 31
                month += 1
            }
        }
        when {
            month > 12 -> {
                month = 1
                year += 1
            }
        }
        return LocalDateTime.of(year, month, dayOfMonth, hourInt, minuteInt)

    }

    private fun getOpeningHoursWithoutDetails(openingHours: OpeningHours?): String {

        val openStatus: String = if (openingHours != null) {
            when {
                openingHours.openNow!! -> {
                    application.getString(R.string.open)
                }
                else -> {
                    application.getString(R.string.closed)
                }
            }
        } else {
            application.getString(R.string.opening_hours_unavailable)
        }
        return openStatus
    }

    private fun getCurrentNumericDay(): Int {
        val currentDate = LocalDateTime.now(clock)
        var dayOfWeek = currentDate.dayOfWeek.value
        // CONVERT SUNDAY TO 0 (NEEDED TO GET SAME DAY AS OPENING HOURS)
        when (dayOfWeek) {
            7 -> {
                dayOfWeek = 0
            }
        }
        return dayOfWeek
    }

    private fun currentYear(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.year
    }

    private fun currentMonth(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.monthValue
    }

    private fun getCurrentDayOfMonth(): Int {
        val currentDate = LocalDate.now(clock)
        return currentDate.dayOfMonth
    }

    // RETURN TRUE IF 30 DAYS MONTH
    private fun isEvenMonth(): Boolean {
        return currentMonth() == 2
                || currentMonth() == 4
                || currentMonth() == 6
                || currentMonth() == 9
                || currentMonth() == 11
    }

    private fun isConsiderClosestThanSelected(
        selectedHour: LocalDateTime?,
        hourToConsider: LocalDateTime
    ): Boolean {
        return when (selectedHour) {
            null -> {
                true
            }
            else -> hourToConsider.isBefore(selectedHour)
        }
    }

    private fun getReadableHour(selectedHour: LocalDateTime): String {

        val minReadable: String
        val meridian: String
        var hour: Int = selectedHour.hour
        val minutes: Int = selectedHour.minute

        when {
            hour > 12 -> {
                hour -= 12
                meridian = application.getString(R.string.pm)
            }
            else -> {
                meridian = application.getString(R.string.am)
            }
        }
        minReadable = when {
            minutes == 0 -> {
                application.getString(R.string.dot)
            }
            minutes < 10 -> {
                application.getString(R.string.two_dots_for_minutes) + minutes
            }
            else -> {
                application.getString(R.string.two_dots) + minutes
            }
        }
        return " $hour$minReadable$meridian"
    }

    private fun getReadableDay(selectedOpeningDateTime: LocalDateTime): String {

        return when {
            selectedOpeningDateTime.dayOfWeek != LocalDateTime.now(clock).dayOfWeek -> {
                val str: String =
                    when (selectedOpeningDateTime.dayOfWeek.value) {
                        LocalDateTime.now(clock).dayOfWeek.value + 1 -> {
                            application.getString(R.string.tomorrow)
                        }
                        else -> {
                            selectedOpeningDateTime.dayOfWeek.toString().lowercase(Locale.ROOT)
                        }
                    }
                " " + str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
            }
            else -> {
                ""
            }
        }
    }

    private fun convertRatingStars(rating: Double): Double {
        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        return (rating * 3 / 5).roundToInt().toDouble()
    }

    private fun usersWhoChoseThisRestaurant(
        restaurantId: String?,
        userWhoMadeRestaurantChoice: List<UserWhoMadeRestaurantChoice>
    ): String {

        var likes = 0

        for (i in userWhoMadeRestaurantChoice.indices) {
            when (userWhoMadeRestaurantChoice[i].restaurantId) {
                restaurantId -> {
                    likes += 1
                }
            }
        }
        val likeAsString: String = when {
            likes != 0 -> {
                application.getString(R.string.left_bracket) + likes + application.getString(R.string.right_bracket)
            }
            else -> {
                ""
            }
        }
        return likeAsString
    }

    private fun getTextColor(openingHours: String): Int {

        var textColor = Color.GRAY

        when (openingHours) {
            application.getString(R.string.closing_soon) -> {
                textColor = Color.RED
            }
        }
        return textColor
    }
}