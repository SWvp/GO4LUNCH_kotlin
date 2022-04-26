package com.kardabel.go4lunch.ui.main

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice
import com.kardabel.go4lunch.pojo.Predictions
import com.kardabel.go4lunch.repository.LocationRepository
import com.kardabel.go4lunch.repository.UserSearchRepository
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository
import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase
import com.kardabel.go4lunch.util.PermissionsViewAction
import com.kardabel.go4lunch.util.SingleLiveEvent

class MainActivityViewModel constructor(
    private val application: Application,
    private val locationRepository: LocationRepository,
    private val getPredictionsUseCase: GetPredictionsUseCase,
    private val userSearchRepository: UserSearchRepository,
    private val usersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    val actionSingleLiveEvent: SingleLiveEvent<PermissionsViewAction> = SingleLiveEvent()
    val predictionsMediatorLiveData = MediatorLiveData<List<PredictionViewState>>()
    val mainActivityYourLunchViewStateMediatorLiveData =
        MediatorLiveData<MainActivityYourLunchViewState>()

    // CHECK PERMISSIONS
    fun checkPermission(activity: Activity?) {
        when {
            ContextCompat.checkSelfPermission(
                application,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                permission.ACCESS_FINE_LOCATION
            ) -> {
                actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_DENIED)
            }
            else -> {
                actionSingleLiveEvent.setValue(PermissionsViewAction.PERMISSION_ASKED)
            }
        }
    }

    // WHEN PERMISSION IS GRANTED, RETRIEVE USER LOCATION
    private fun permissionGranted() {
        locationRepository.StartLocationRequest()
    }

    // WHEN CLICKING ON SEARCH VIEW WE PASSED THE TEXT TO USE CASE AND THEN OBSERVE IT
    fun sendTextToAutocomplete(text: String?) {
        val predictionsLiveData: LiveData<Predictions> = getPredictionsUseCase.invoke(text)
        predictionsMediatorLiveData.addSource(
            predictionsLiveData
        ) { predictions: Predictions? ->
            this.combine(
                predictions
            )
        }
    }

    private fun combine(predictions: Predictions?) {
        if (predictions != null) {
            predictionsMediatorLiveData.value = map(predictions)
        }
    }

    // MAP THE PREDICTIONS RESULT TO VIEW STATE
    private fun map(predictions: Predictions): List<PredictionViewState> {
        val predictionsList: MutableList<PredictionViewState> = ArrayList()
        for ((description, structuredFormatting, placeId) in predictions.predictions!!) {
            predictionsList.add(
                PredictionViewState(
                    description!!,
                    placeId!!,
                    structuredFormatting!!.name!!
                )
            )
        }
        return predictionsList
    }

    // RETRIEVE THE CURRENT USER RESTAURANT CHOICE
    fun getUserRestaurantChoice() {
        val workmatesWhoMadeRestaurantChoiceLiveData: LiveData<List<UserWhoMadeRestaurantChoice?>> =
            usersWhoMadeRestaurantChoiceRepository.workmatesWhoMadeRestaurantChoice
        mainActivityYourLunchViewStateMediatorLiveData.addSource(
            workmatesWhoMadeRestaurantChoiceLiveData
        ) { userWhoMadeRestaurantChoices ->
            mapUserRestaurantChoice(userWhoMadeRestaurantChoices)
        }
    }

    private fun mapUserRestaurantChoice(userWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice?>) {
        val currentUserId: String = getCurrentUserIdUseCase.invoke()
        var yourLunch = MainActivityYourLunchViewState(
            application.getString(R.string.no_current_user_restaurant_choice),
            0
        )
        for (workmate in userWhoMadeRestaurantChoices) {
            if (workmate != null) {
                if (workmate.userId == currentUserId) {
                    yourLunch = MainActivityYourLunchViewState(
                        workmate.restaurantId,
                        1
                    )
                }
            }
        }
        mainActivityYourLunchViewStateMediatorLiveData.value = yourLunch
    }

    fun userSearch(predictionText: String?) {
        userSearchRepository.usersSearch(predictionText)
    }
}
