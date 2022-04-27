package com.kardabel.go4lunch.ui.workmates

import com.kardabel.go4lunch.repository.WorkmatesRepository
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData
import com.kardabel.go4lunch.model.UserModel
import com.kardabel.go4lunch.model.UserWhoMadeRestaurantChoice
import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import com.kardabel.go4lunch.R
import androidx.lifecycle.LiveData
import java.lang.Boolean.compare
import java.util.*

class WorkMatesViewModel constructor(
    private val application: Application,
    private val workmatesRepository: WorkmatesRepository,
    private val usersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository,
) : ViewModel() {

    // HERE WE HAVE 2 COLLECTIONS TO OBSERVE:
    // ONE WITH ALL REGISTERED USERS
    // AND ONE WITH USERS (WORKMATES) WHO MADE A CHOICE
    private var workMatesLiveData: LiveData<List<UserModel>> = workmatesRepository.workmates
    private var workmatesWhoMadeChoiceLiveData: LiveData<List<UserWhoMadeRestaurantChoice>> =
        usersWhoMadeRestaurantChoiceRepository.workmatesWhoMadeRestaurantChoice

    val workMatesViewStateMediatorLiveData =
        MediatorLiveData<List<WorkMateViewState>>().apply {
            addSource(workMatesLiveData) { workmates ->
                combine(
                    workmates,
                    workmatesWhoMadeChoiceLiveData.value
                )
            }
            addSource(workmatesWhoMadeChoiceLiveData) { workmatesWhoMadeRestaurantChoices ->
                combine(
                    workMatesLiveData.value,
                    workmatesWhoMadeRestaurantChoices
                )
            }
        }

    // COMBINE THE 2 SOURCES
    private fun combine(
        workmates: List<UserModel>?,
        workmatesWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?,
    ) {
        workMatesViewStateMediatorLiveData.value = mapWorkmates(
            workmates,
            workmatesWhoMadeRestaurantChoices
        )
    }

    // MAP TO WORKMATE VIEW STATE
    @SuppressLint("ResourceType")
    private fun mapWorkmates(
        workmates: List<UserModel>?,
        workmatesWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?
    ): List<WorkMateViewState> {

        val workMateListViewState = mutableListOf<WorkMateViewState>()

        if (workmates != null) {
            for (workmate in workmates) {
                val workmateName = workmate.userName
                val avatar = workmate.avatarURL
                val workmateId = workmate.uid
                val workmateChoice: String =
                    workmateChoice(workmateId!!, workmatesWhoMadeRestaurantChoices)
                var gotRestaurant = false
                var colorText = Color.GRAY
                if (workmateChoice != " " + application.getString(R.string.not_decided)) {
                    colorText = Color.BLACK
                    gotRestaurant = true
                }

                workMateListViewState.add(
                    WorkMateViewState(
                        workmateName = workmateName,
                        workmateDescription = workmateName + workmateChoice,
                        workmatePhoto = avatar,
                        workmateId = workmateId,
                        gotRestaurant = gotRestaurant,
                        textColor = colorText
                    )
                )

            }
        }
        // SORT THE LIST BY BOOLEAN, IF TRUE, APPEARS AT THE TOP OF THE LIST
        workMateListViewState.sortWith(Comparator { (_, _, _, _, gotRestaurant), (_, _, _, _, gotRestaurant1) ->
            compare(
                !gotRestaurant,
                !gotRestaurant1
            )
        })
        return workMateListViewState
    }

    private fun workmateChoice(
        workmateId: String,
        workmatesWhoMadeRestaurantChoices: List<UserWhoMadeRestaurantChoice>?
    ): String {

        var restaurantName = " " + application.getString(R.string.not_decided)

        workmatesWhoMadeRestaurantChoices?.let { workmates ->
            workmates.forEach { workmate ->
                if (workmate.userId.equals(workmateId)) {
                    restaurantName =
                        " " +
                                application.getString(R.string.left_bracket) +
                                workmate.restaurantName +
                                application.getString(R.string.right_bracket)
                }

            }
        }
        return restaurantName
    }
}